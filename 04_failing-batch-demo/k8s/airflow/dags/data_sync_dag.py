from airflow import DAG
from airflow.providers.cncf.kubernetes.operators.pod import KubernetesPodOperator
from datetime import datetime, timedelta

default_args = {
    'owner': 'ad-batch-demo',
    'depends_on_past': False,
    'start_date': datetime(2026, 4, 1),
    'retries': 0,
}

with DAG(
    dag_id='data_sync_batch',
    default_args=default_args,
    description='데이터 동기화 배치 (실패 시뮬레이션)',
    schedule=None,
    catchup=False,
    is_paused_upon_creation=True,
    tags=['ad-batch', 'demo', 'failing'],
) as dag:

    run_data_sync = KubernetesPodOperator(
        task_id='run_data_sync',
        name='data-sync-batch',
        namespace='ad-batch',
        image='kyong0409/failing-batch-demo:latest',
        image_pull_policy='Always',
        env_vars={
            'SPRING_PROFILES_ACTIVE': 'k8s',
        },
        on_finish_action='delete_succeeded_pod',
        get_logs=True,
        startup_timeout_seconds=300,
        container_resources={
            'limits': {'memory': '512Mi', 'cpu': '500m'},
            'requests': {'memory': '256Mi', 'cpu': '250m'},
        },
    )
