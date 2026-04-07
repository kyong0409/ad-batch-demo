# AD Batch Demo

Airflow KubernetesPodOperator 기반 배치 스케줄링 데모 프로젝트입니다.  
Spring Batch Job을 Kubernetes Pod으로 실행하고, 배치 관리 UI에서 이력을 조회하는 전체 흐름을 체험할 수 있습니다.

---

## 업데이트 내역

### macOS 지원

- **macOS (Apple Silicon)** 환경에서도 실행 가능하도록 스크립트 수정
- 모든 k8s-scripts에 **실행 권한(`chmod +x`)** 자동 부여 처리
- `08_port_forward.sh`에서 OS를 감지하여 macOS/Linux에서는 `sudo`로 포트 포워딩 실행 (Windows에서는 불필요)
- Gradle 빌드를 **멀티스테이지 Dockerfile** 내에서 수행하도록 변경하여 호스트에 Java 설치 불필요

### macOS Docker Desktop 설정 (Apple Silicon)

Apple Silicon Mac에서 Docker Desktop 사용 시 Rosetta 설정이 필요합니다.

1. Rosetta 설치:
   ```bash
   softwareupdate --install-rosetta
   ```
2. Docker Desktop → Settings → General → **"Use Rosetta for x86_64/amd64 emulation on Apple Silicon"** 체크 활성화

### 병렬 배포 스크립트 추가

`00-1_build_all.sh` 스크립트가 추가되었습니다. 01~06번 이미지를 Docker Hub에서 pull 받아 **병렬로 클러스터에 배포**합니다.

```bash
cd k8s-scripts
./00_setup.sh          # 1) 인프라 셋업 + DAG 등록 + Airflow 설치
./00-1_build_all.sh    # 2) 전체 이미지 pull & 병렬 배포
./08_port_forward.sh   # 3) 포트 포워딩
```

---

## 프로젝트 구성

| 번호 | 디렉토리 | 설명 | 기술 스택 |
|------|---------|------|----------|
| 01 | `01_batch_demo` | 광고 성과 집계 배치 Job (성공) | Spring Boot, Spring Batch, MSSQL |
| 02 | `02_dscore-batch-ui` | 배치 관리 UI | Vue.js, Nginx |
| 03 | `03_batch-api` | 배치 관리 API 서버 | Spring Boot, MyBatis, MSSQL |
| 04 | `04_failing-batch-demo` | 데이터 동기화 배치 Job (실패 - 즉시) | Spring Boot, Spring Batch |
| 05 | `05_enterprise-batch` | 엔터프라이즈 광고 집계 배치 Job (성공) | Spring Boot, Spring Batch, MSSQL |
| 06 | `06_enterprise-failing-batch` | 주문 동기화 배치 Job (실패 - skip 초과) | Spring Boot, Spring Batch, MSSQL |

### Job 설명

| Job | DAG ID | 결과 | 동작 방식 |
|-----|--------|------|----------|
| 01 - 광고 성과 집계 | `daily_ad_stats_batch` | 성공 | DB에서 광고 데이터를 읽어 일별 통계를 집계 |
| 04 - 데이터 동기화 | `data_sync_batch` | 실패 | 인메모리 데이터 5건 처리 중 4번째에서 RuntimeException 발생하여 즉시 실패 |
| 05 - 엔터프라이즈 광고 집계 | `enterprise_ad_stats_batch` | 성공 | DB 파티셔닝 + CSV 내보내기가 포함된 고급 배치 |
| 06 - 주문 동기화 | `enterprise_order_sync_batch` | 실패 | 불량 데이터를 skip하며 처리하다가 skipLimit(3) 초과로 실패 |

<details>
<summary>01 - 광고 성과 집계 (dailyAdStatsJob) 상세 흐름</summary>

어제 날짜의 광고 클릭 로그를 campaign/ad 별로 집계하여 일별 통계 테이블에 저장합니다.

```
ad_click_log 테이블 (어제 데이터)
    │
    ▼
┌─────────────────── dailyAdStatsStep (chunk=10) ───────────────────┐
│                                                                    │
│  [Reader]  JDBC로 campaign_id, ad_id 별 impression/click 집계 조회  │
│      ▼                                                             │
│  [Processor]  createdAt 타임스탬프 세팅 + CTR 로깅                  │
│      ▼                                                             │
│  [Writer]  daily_ad_stats 테이블에 MERGE (upsert)                  │
│                                                                    │
└────────────────────────────────────────────────────────────────────┘
```

</details>

<details>
<summary>04 - 데이터 동기화 (dataSyncJob) 상세 흐름</summary>

CRM/ERP/HR → Data Warehouse 동기화를 시뮬레이션합니다. 4번째 항목에서 의도적으로 실패합니다.

```
In-Memory 리스트 [SYNC-001 ~ SYNC-005]
    │
    ▼
┌─────────────────── dataSyncStep (chunk=10) ────────────────────────┐
│                                                                     │
│  [Reader]  ListItemReader (5건: CRM, ERP, HR 소스)                  │
│      ▼                                                              │
│  [Processor]  데이터 변환 처리                                       │
│      ▼         ⚠ SYNC-004에서 RuntimeException 발생!                │
│               "ERP API 응답 타임아웃 (30s)"                          │
│      ▼                                                              │
│  [Writer]  동기화 완료 로깅                                          │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
    │
    ▼
  Job FAILED (3/5건 처리, SYNC-004에서 실패)
```

</details>

<details>
<summary>05 - 엔터프라이즈 광고 집계 (enterpriseAdStatsJob) 상세 흐름</summary>

검증 → 정리 → 캠페인별 파티션 병렬 집계 → CTR 계산 → 검증 → CSV 내보내기 → 완료의 7단계로 구성된 엔터프라이즈급 배치입니다.

```
Step 1 ▶ dataValidationStep (Tasklet)
          │  ent_ad_click_log에 대상 날짜 데이터 존재 확인
          │  없으면 → Job 종료  /  있으면 → sourceRowCount 저장 후 계속
          ▼
Step 2 ▶ cleanupStep (Tasklet)
          │  ent_daily_ad_stats에서 대상 날짜 기존 데이터 삭제
          │  (재실행 안전성 보장)
          ▼
Step 3 ▶ aggregationStep (Partitioned, 병렬 3스레드)
          │
          │  ┌─ CampaignPartitioner ─────────────────────────┐
          │  │  campaign_id별로 파티션 분할                     │
          │  └───────────────────────────────────────────────┘
          │       ▼              ▼              ▼
          │  [Partition 0]  [Partition 1]  [Partition 2] ...
          │       │
          │       ▼  aggregationWorkerStep (chunk=500)
          │  ┌─────────────────────────────────────────┐
          │  │ Reader: campaign별 클릭 로그 조회          │
          │  │    ▼                                      │
          │  │ Processor (4단계 CompositeProcessor):     │
          │  │   1. DataCleansing → trim + UPPERCASE     │
          │  │   2. Validation   → 필수값 검증            │
          │  │   3. EventToStats → IMPRESSION/CLICK 변환  │
          │  │   4. TimestampEnrichment → createdAt 세팅  │
          │  │    ▼                                      │
          │  │ Writer: ent_daily_ad_stats에 MERGE        │
          │  │   (ROWLOCK + 지수 백오프 재시도 최대 10회)  │
          │  └─────────────────────────────────────────┘
          ▼
Step 4 ▶ ctrUpdateStep (Tasklet)
          │  CTR = click_count / impression_count 일괄 UPDATE
          ▼
Step 5 ▶ verificationStep (Tasklet)
          │  소스 건수 vs 집계 건수 비교 + 파티션 실패 여부 확인
          ▼
Step 6 ▶ csvExportStep (Chunk, size=100)
          │  ent_daily_ad_stats → ./output/daily_ad_stats_[DATE].csv
          ▼
Step 7 ▶ completionStep (Tasklet)
             최종 요약 로깅 (대상일, 소스건수, 집계건수, CSV 경로)
```

</details>

<details>
<summary>06 - 주문 동기화 (orderSyncJob) 상세 흐름</summary>

PENDING 상태의 주문을 동기화하며, 유효성 검증 실패 시 Skip 처리합니다. Skip이 3건을 초과하면 Job이 실패합니다.

```
ent_order_sync 테이블 (sync_status = 'PENDING')
    │
    ▼
┌────────────── orderSyncStep (chunk=2, faultTolerant) ──────────────┐
│                                                                     │
│  [Reader]  JdbcPagingReader - PENDING 주문 페이징 조회               │
│      ▼                                                              │
│  [Processor] OrderSyncProcessor                                     │
│      │  1. amount null/0 이하  → DataIntegrityViolationException    │
│      │  2. amount > 1,000,000 → DataIntegrityViolationException    │
│      │  3. customerName 비어있음 → IllegalArgumentException          │
│      │  4. 통과 → OrderSyncResult (status="SYNCED") 생성            │
│      ▼                                                              │
│  [Writer]  ent_order_sync_result에 MERGE (upsert)                  │
│                                                                     │
│  ── Fault Tolerance ──────────────────────────────────────────      │
│  • Skip 대상: DataIntegrityViolationException, IllegalArgException  │
│  • Skip 한도: 최대 3건                                               │
│  • Skip 시: DetailedSkipListener가 원본 테이블에                     │
│             sync_status='FAILED' + error_message 업데이트            │
│  • 3건 초과 Skip → Step FAILED                                      │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
    │
    ▼
  FailureNotificationListener
    • 성공 시: 완료 로깅
    • 실패 시: 상세 에러 리포트 + Slack/이메일 알림 시뮬레이션
```

</details>

> **단순하게 Pod 생성 및 Job 성공/실패만 확인하려면 01, 04번 Job만 실행하면 됩니다.**  
> 05, 06번은 엔터프라이즈 패턴(파티셔닝, fault tolerant skip 정책 등)을 보여주기 위한 것으로, 필수 실행 대상이 아닙니다.

---

## 1. 프로젝트 repository를 clone 하여 사용하는 경우

### 1-0. 요약
minikube 사용시 k8s-script/ 폴더에서 00 ~ 08번 스크립트를 순서대로 실행하면 
airflow + batch api + batch 이력 조회 ui + 4개의 job 으로 이루어진 환경이 구성됩니다.

### 1-1. 사전 요구사항

- Docker Desktop (또는 Docker Engine)
- kubectl
- Helm 3
- 로컬 Kubernetes 클러스터 (minikube 기본)

### 1-2. 로컬 클러스터 설정

#### minikube (기본)

스크립트가 minikube 기준으로 작성되어 있어 minikube 사용시 별도 설정 없이 사용 가능합니다.

- Windows: [minikube 설치](https://minikube.sigs.k8s.io/docs/start/?arch=%2Fwindows%2Fx86-64%2Fstable%2F.exe+download)
- Mac:
  ```bash
  brew install minikube
  ```

```bash
minikube start --memory=8192 --cpus=4
```

<details>
<summary>kind 사용 시 수정 필요한 부분</summary>

**1) 클러스터 시작 (`00_setup.sh`)**

```bash
# minikube 관련 코드를 아래로 교체
kind create cluster --name ad-batch
# ingress addon 대신 nginx ingress 수동 설치
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml
```

**2) 이미지 로드 (`01~06` 스크립트 전부)**

```bash
# 변경 전 (minikube)
minikube ssh -- docker rmi -f "docker.io/library/${IMAGE_NAME}:latest" &>/dev/null || true
minikube image load "${IMAGE_NAME}:latest"

# 변경 후 (kind)
kind load docker-image "${IMAGE_NAME}:latest" --name ad-batch
```

</details>

<details>
<summary>k3d 사용 시</summary>

```bash
# 클러스터 시작 (traefik ingress 내장)
k3d cluster create ad-batch

# 이미지 로드
k3d image import "${IMAGE_NAME}:latest" -c ad-batch
```

</details>

> **나머지 kubectl, helm 명령은 클러스터 종류와 무관하게 동일합니다.**

### 1-3. 스크립트 실행 순서

`k8s-scripts/` 디렉토리에서 번호 순서대로 실행합니다.

```bash
cd k8s-scripts

# 1단계: 인프라 셋업 (minikube, namespace, MSSQL, DAG 등록, Airflow 설치, Ingress)
./00_setup.sh

# 2단계: 전체 이미지 pull & 병렬 배포
./00-1_build_all.sh

# 3단계: 포트 포워딩 (Ingress 접속)
./08_port_forward.sh
```

> `00-1_build_all.sh`는 01~06번 이미지를 Docker Hub에서 pull 받아 병렬로 클러스터에 배포합니다.  
> 개별 빌드가 필요한 경우 `01_api_update_image.sh` ~ `06_enterprise_failing_batch_update_image.sh`를 직접 실행할 수 있습니다.

### 1-4. hosts 파일 설정

포트 포워딩 실행 후 hosts 파일에 아래 내용을 추가해야 합니다.

```
# Windows: C:\Windows\System32\drivers\etc\hosts
# macOS/Linux: /etc/hosts

127.0.0.1  ui.batch.local airflow.batch.local
```

### 1-5. 접속 URL

| 서비스 | URL | 비고 |
|--------|-----|------|
| Batch UI | http://ui.batch.local | 배치 이력/스케줄 관리 |
| Batch API | http://ui.batch.local/api | REST API (UI와 같은 도메인, path 기반) |
| Airflow UI | http://airflow.batch.local | 계정: `admin` / `admin` |

### 1-6. DAG 스케줄 관련 참고사항

모든 DAG의 `schedule`은 `None`으로 설정되어 있습니다.

데모 환경에서 DAG가 처음 등록될 때 자동 스케줄 실행이 발생하여 수동 trigger와 중복 실행되는 것을 방지하기 위한 조치입니다. **Airflow UI에서 수동으로 trigger해야만 실행됩니다.**

### 1-7. Job 실행 확인

Airflow에서 DAG를 trigger한 후 아래 명령어로 Pod 상태를 실시간 모니터링할 수 있습니다.

```bash
kubectl get pod -n ad-batch -w
```

> `-w` 옵션은 watch 모드로, Pod 상태가 변경될 때마다 자동으로 출력됩니다. `Ctrl+C`로 종료합니다.

#### Pod 상태(STATUS) 설명

| 상태 | 설명 |
|------|------|
| `Pending` | Pod이 생성 요청되었으나 아직 노드에 스케줄링되지 않은 상태. 이미지 pull 대기 중일 수 있음 |
| `ContainerCreating` | 노드에 스케줄링되어 컨테이너 이미지를 pull하고 컨테이너를 생성하는 중 |
| `Running` | 컨테이너가 실행 중. Spring Batch Job이 동작하고 있는 상태 |
| `Completed` | 컨테이너가 정상 종료 (exit code 0). Job이 성공적으로 완료됨 |
| `Error` | 컨테이너가 비정상 종료 (exit code ≠ 0). Job 실행 중 예외가 발생하여 실패함 |
| `ErrImagePull` / `ImagePullBackOff` | 컨테이너 이미지를 찾을 수 없음. 이미지가 클러스터에 로드되지 않았을 때 발생 |
| `CrashLoopBackOff` | 컨테이너가 반복적으로 시작 직후 종료되는 상태. 설정 오류나 DB 연결 실패 등이 원인 |

#### 성공/실패에 따른 Pod 정리

모든 DAG에 `on_finish_action='delete_succeeded_pod'`가 설정되어 있어, Job 결과에 따라 Pod 처리가 달라집니다.

| Job 결과 | Pod 상태 | Pod 정리 | 이유 |
|----------|---------|---------|------|
| **성공** | `Completed` | 자동 삭제됨 | Airflow가 로그 수집 후 성공한 Pod을 자동으로 정리 |
| **실패** | `Error` | 삭제되지 않고 남아 있음 | 디버깅을 위해 Pod이 유지됨. 로그 확인 후 수동 삭제 필요 |

실패한 Pod의 로그를 확인하고 정리하려면:

```bash
# 실패한 Pod 로그 확인
kubectl logs <pod-name> -n ad-batch

# 확인 후 수동 삭제
kubectl delete pod <pod-name> -n ad-batch
```

#### 데모 Job별 예상 결과

| Job | DAG ID | 예상 Pod 상태 | Pod 잔존 여부 |
|-----|--------|--------------|-------------|
| 01 - 광고 성과 집계 | `daily_ad_stats_batch` | `Completed` → 자동 삭제 | X |
| 04 - 데이터 동기화 | `data_sync_batch` | `Error` → 남아 있음 | O |
| 05 - 엔터프라이즈 집계 | `enterprise_ad_stats_batch` | `Completed` → 자동 삭제 | X |
| 06 - 주문 동기화 | `enterprise_order_sync_batch` | `Error` → 남아 있음 | O |

### 1-8. UI 기능 제한 사항

Batch UI에서 아래 기능들은 API 연동이 구현되어 있지 않습니다.

- 배치 재실행 (Airflow API 연동 필요)
- 엑셀 다운로드
- 배치 스케줄 등록/수정 (조회만 가능)

이력 조회 및 상세 보기는 정상 동작합니다.

### 1-9. 이미지 정리

빌드를 반복하면 minikube에 이전 이미지가 쌓입니다. 정리가 필요할 때:

```bash
./09_cleanup_images.sh
```

---

## 2. Docker Hub에서 이미지를 받아 사용하는 경우

> **스크립트 사용을 권장합니다.** 위 1번 방법(repository clone)의 스크립트를 사용하면 아래 과정이 자동으로 처리됩니다.

### 2-1. 공유되는 이미지 목록

| 이미지명 | 설명 |
|---------|------|
| `kyong0409/batch-api` | 배치 관리 API 서버 |
| `kyong0409/batch-ui` | 배치 관리 UI |
| `kyong0409/ad-batch-demo` | Job 01 - 광고 성과 집계 (성공) |
| `kyong0409/failing-batch-demo` | Job 04 - 데이터 동기화 (실패) |
| `kyong0409/enterprise-batch` | Job 05 - 엔터프라이즈 집계 (성공) |
| `kyong0409/enterprise-failing-batch` | Job 06 - 주문 동기화 (실패) |

<details>
<summary>수동 설치 절차 (펼치기)</summary>

### 2-2. 사전 요구사항

- Docker Desktop (또는 Docker Engine)
- kubectl
- Helm 3
- 로컬 Kubernetes 클러스터 (minikube, kind, k3d 등)

> 프로젝트 소스 코드, Java, Node.js, Gradle 등은 **필요하지 않습니다.**

### 2-3. 설치 절차

#### Step 1: 이미지 Pull

```bash
docker pull kyong0409/batch-api:latest
docker pull kyong0409/batch-ui:latest
docker pull kyong0409/ad-batch-demo:latest
docker pull kyong0409/failing-batch-demo:latest
docker pull kyong0409/enterprise-batch:latest           # 선택
docker pull kyong0409/enterprise-failing-batch:latest   # 선택
```

#### Step 2: 로컬 클러스터에 이미지 로드

```bash
# minikube
minikube image load kyong0409/batch-api:latest
minikube image load kyong0409/batch-ui:latest
minikube image load kyong0409/ad-batch-demo:latest
minikube image load kyong0409/failing-batch-demo:latest
minikube image load kyong0409/enterprise-batch:latest           # 선택
minikube image load kyong0409/enterprise-failing-batch:latest   # 선택
```

<details>
<summary>kind / k3d 사용 시</summary>

```bash
# kind
kind load docker-image kyong0409/batch-api:latest kyong0409/batch-ui:latest kyong0409/ad-batch-demo:latest kyong0409/failing-batch-demo:latest

# k3d
k3d image import kyong0409/batch-api:latest kyong0409/batch-ui:latest kyong0409/ad-batch-demo:latest kyong0409/failing-batch-demo:latest
```

</details>

#### Step 3: Namespace 및 MSSQL 배포

```bash
# Namespace 생성
kubectl create namespace ad-batch

# MSSQL 배포 (아래 manifest를 mssql.yml로 저장 후 apply)
kubectl apply -f mssql.yml
```

> MSSQL manifest는 프로젝트의 `01_batch_demo/k8s/mssql-deployment.yml`을 참고하세요.  
> 이미 MSSQL이 있다면 `ad_batch_demo` 데이터베이스만 생성하면 됩니다.  
> 테이블과 데이터는 각 앱이 시작할 때 자동으로 생성합니다.

#### Step 4: Airflow 설치

```bash
helm repo add apache-airflow https://airflow.apache.org
helm repo update

helm upgrade --install airflow apache-airflow/airflow \
  --namespace ad-batch \
  --create-namespace \
  --timeout 30m
```

> Airflow values 파일은 프로젝트의 `01_batch_demo/k8s/airflow/values.yml`을 참고하세요.  
> DAG를 ConfigMap으로 마운트하는 설정이 포함되어 있습니다.

#### Step 5: Batch API 및 UI 배포

```bash
# Batch API
kubectl create deployment batch-api --image=kyong0409/batch-api:latest -n ad-batch
kubectl set env deployment/batch-api SPRING_PROFILES_ACTIVE=k8s -n ad-batch
kubectl expose deployment batch-api --name=batch-api-service --port=8080 -n ad-batch

# Batch UI
kubectl create deployment batch-ui --image=kyong0409/batch-ui:latest -n ad-batch
kubectl expose deployment batch-ui --name=batch-ui-service --port=80 -n ad-batch
```

> Batch UI의 API Base URL은 `/api`(상대 경로)로 빌드 시 설정되어 있습니다.  
> Ingress에서 `ui.batch.local/api` 경로를 batch-api-service로 라우팅하므로 별도 도메인 설정이 필요 없습니다.

#### Step 6: Ingress 설정

```yaml
# ingress.yml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: batch-ingress
  namespace: ad-batch
spec:
  ingressClassName: nginx
  rules:
    - host: ui.batch.local
      http:
        paths:
          - path: /api
            pathType: Prefix
            backend:
              service:
                name: batch-api-service
                port:
                  number: 8080
          - path: /
            pathType: Prefix
            backend:
              service:
                name: batch-ui-service
                port:
                  number: 80
    - host: airflow.batch.local
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: airflow-api-server
                port:
                  number: 8080
```

```bash
kubectl apply -f ingress.yml
```

#### Step 7: DAG 등록

프로젝트 루트의 `dags/` 디렉토리에 Docker Hub 이미지(`kyong0409/...`)를 참조하는 DAG 파일 4개가 준비되어 있습니다.

```bash
kubectl create configmap ad-batch-dags \
  --from-file=dags/ad_batch_dag.py \
  --from-file=dags/data_sync_dag.py \
  --from-file=dags/enterprise_batch_dag.py \
  --from-file=dags/order_sync_dag.py \
  --namespace ad-batch \
  --dry-run=client -o yaml | kubectl apply -f -
```

> ConfigMap 등록 후 Airflow가 DAG를 인식하려면 values.yml에 ConfigMap 마운트 설정이 필요합니다.  
> 프로젝트의 `01_batch_demo/k8s/airflow/values.yml`을 참고하세요.

#### Step 8: hosts 파일 설정 및 포트 포워딩

```
# hosts 파일에 추가
127.0.0.1  ui.batch.local airflow.batch.local
```

```bash
# 포트 포워딩
kubectl port-forward -n ingress-nginx service/ingress-nginx-controller 80:80
```

### 2-4. 접속 확인

| 서비스 | URL | 비고 |
|--------|-----|------|
| Batch UI | http://ui.batch.local | 이력 조회, 스케줄 관리 |
| Batch API | http://ui.batch.local/api | REST API (UI와 같은 도메인) |
| Airflow UI | http://airflow.batch.local | 계정: `admin` / `admin` |

</details>

---

## DB 구성

모든 앱이 동일한 MSSQL 인스턴스의 `ad_batch_demo` 데이터베이스를 공유합니다.

| 테이블 종류 | 생성 주체 | 설명 |
|------------|----------|------|
| `BATCH_JOB_*`, `BATCH_STEP_*` | Spring Batch (자동) | Job 실행 메타데이터 (이력 조회용) |
| `om_batch_sclg_bas` | batch-api (자동) | 배치 스케줄 관리 |
| `ad_daily_stats` 등 | 각 배치 Job (자동) | 비즈니스 테이블 |

> 테이블과 샘플 데이터는 각 앱이 시작할 때 `schema.sql`, `data.sql`로 자동 생성됩니다.  
> MSSQL에 `ad_batch_demo` 데이터베이스만 존재하면 됩니다.

---

## 트러블슈팅

### Airflow에서 trigger 했는데 Pod이 뜨지 않는 경우

```bash
# 이미지가 클러스터에 로드되어 있는지 확인
minikube image ls | grep ad-batch-demo

# Airflow scheduler 로그 확인
kubectl logs -n ad-batch -l component=scheduler --tail=50
```

### Pod이 실행됐지만 DB 연결 실패

```bash
# MSSQL이 정상 실행 중인지 확인
kubectl get pod -n ad-batch -l app=mssql

# MSSQL 접속 테스트 (port-forward 후)
kubectl port-forward svc/mssql-service -n ad-batch 11433:1433
# SQL 클라이언트에서 localhost:11433, sa 계정으로 접속
```

### 07_register_dags.sh 실행 시 타임아웃

Airflow Pod 재시작에 시간이 걸릴 수 있습니다. 타임아웃이 발생해도 Pod이 정상 기동되면 문제없습니다.

```bash
# Pod 상태 확인
kubectl get pod -n ad-batch | grep airflow
```
