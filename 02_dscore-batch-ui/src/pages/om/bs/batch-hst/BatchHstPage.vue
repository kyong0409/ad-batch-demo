<template>
  <div class="page-content">
    <section class="page-header">
      <h1 class="page-title">배치 실행 이력 관리</h1>
    </section>

    <!-- 검색 영역 -->
    <section class="search-section">
      <div class="search-form search-form-multi-row">
        <div class="search-row">
          <div class="form-group">
            <label class="form-label" for="batch-hst-jobname">Job Name</label>
            <input
              id="batch-hst-jobname"
              v-model="searchState.jobName"
              type="text"
              class="form-input"
              placeholder="Job Name을 입력하세요"
              @keyup.enter="fetchBatchHistoryList"
            />
          </div>
          <div class="form-group">
            <label class="form-label" for="batch-hst-instance">Job Instance ID</label>
            <input
              id="batch-hst-instance"
              v-model="searchState.jobInstanceId"
              type="text"
              class="form-input"
              placeholder="Job Instance ID를 입력하세요"
              @keyup.enter="fetchBatchHistoryList"
            />
          </div>
          <div class="form-group">
            <label class="form-label" for="batch-hst-execution">Job Execution ID</label>
            <input
              id="batch-hst-execution"
              v-model="searchState.jobExecutionId"
              type="text"
              class="form-input"
              placeholder="Job Execution ID를 입력하세요"
              @keyup.enter="fetchBatchHistoryList"
            />
          </div>
        </div>
        <div class="search-row">
          <div class="form-group date-range-group">
            <label class="form-label">배치수행기간</label>
            <div class="date-range">
              <input v-model="searchState.startTime" type="date" class="form-input" />
              <span class="date-separator">~</span>
              <input v-model="searchState.endTime" type="date" class="form-input" />
            </div>
          </div>
          <div class="form-group">
            <label class="form-label" for="batch-hst-status">작업상태</label>
            <select id="batch-hst-status" v-model="searchState.status" class="form-select">
              <option v-for="opt in options.BATCH_STATUS" :key="opt.value" :value="opt.value">{{ opt.label }}</option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-label" for="batch-hst-result">작업결과</label>
            <select id="batch-hst-result" v-model="searchState.exitCode" class="form-select">
              <option v-for="opt in options.BATCH_STATUS" :key="opt.value" :value="opt.value">{{ opt.label }}</option>
            </select>
          </div>
          <div class="form-actions">
            <button class="btn btn-default" @click="resetSearch">초기화</button>
            <button class="btn btn-primary" @click="fetchBatchHistoryList">검색</button>
          </div>
        </div>
      </div>
    </section>

    <!-- 배치 실행 이력 리스트 -->
    <section class="list-section">
      <div class="list-header">
        <h2 class="section-title">
          배치 실행 이력
          <span class="total-count">(총 {{ pageParams.totalElements }}건)</span>
        </h2>
        <div class="header-actions">
          <button class="btn btn-success" @click="handleExcelDownload">엑셀 다운로드</button>
          <button
            class="btn btn-warning"
            :disabled="!canRestart"
            @click="handleRestart"
          >
            배치 재실행
          </button>
        </div>
      </div>

      <div class="table-wrapper">
        <table class="data-table">
          <thead>
            <tr>
              <th class="checkbox-cell">
                <input type="checkbox" class="table-checkbox" aria-label="배치 이력 전체 선택" @change="toggleAllRows" />
              </th>
              <th v-for="col in BATCH_HST_CONSTANTS.TABLE_HEADERS" :key="col.field">
                {{ col.label }}
              </th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="row in batchData"
              :key="row.jobExecutionId"
              class="table-row-clickable"
            >
              <td class="checkbox-cell" @click.stop>
                <input
                  type="checkbox"
                  class="table-checkbox"
                  :checked="isRowSelected(row)"
                  :aria-label="'배치 이력 ' + row.jobName + ' 선택'"
                  @change="toggleRowSelection(row)"
                />
              </td>
              <td @click="onRowClick(row)">{{ row.rowNum }}</td>
              <td @click="onRowClick(row)" class="link-cell">{{ row.jobName }}</td>
              <td @click="onRowClick(row)">{{ row.jobInstanceId }}</td>
              <td @click="onRowClick(row)">{{ row.jobExecutionId }}</td>
              <td @click="onRowClick(row)">{{ row.startTime }}</td>
              <td @click="onRowClick(row)">{{ row.endTime }}</td>
              <td @click="onRowClick(row)">
                <span :class="getStatusClass(row.status)">{{ row.statusLabel }}</span>
              </td>
              <td @click="onRowClick(row)">
                <span :class="getExitCodeClass(row.exitCode)">{{ row.exitCodeLabel }}</span>
              </td>
              <td @click="onRowClick(row)" class="error-message-cell">{{ row.exitMessage }}</td>
            </tr>
            <tr v-if="batchData.length === 0">
              <td colspan="10" class="table-empty">
                {{ loading ? '불러오는 중...' : '배치 실행 이력이 없습니다.' }}
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 페이지네이션 -->
      <BasePagination
        v-model:page="currentPage"
        v-model:size="pageSize"
        :total-elements="pageParams.totalElements"
        @change="fetchBatchHistoryList"
      />
    </section>

    <!-- 배치 실행 상세 다이얼로그 -->
    <BaseDialog v-model="detailOpen" title="배치 실행 상세" size="lg">
      <!-- Step 정보 -->
      <div class="info-section">
        <label class="info-label">Step 정보</label>
        <pre class="code-block">{{ formatStepInfo(selectedExecution.steps) }}</pre>
      </div>

      <!-- 파라미터 정보 -->
      <div class="info-section">
        <label class="info-label">파라미터 정보</label>
        <pre class="code-block">{{ formatParameterInfo(selectedExecution.parameters) }}</pre>
      </div>

      <!-- 에러메세지 -->
      <div v-if="selectedExecution.exitMessage" class="info-section">
        <label class="info-label">에러메세지</label>
        <pre class="code-block error-message">{{ selectedExecution.exitMessage }}</pre>
      </div>

      <template #actions>
        <button class="btn btn-primary" @click="closeDetail">확인</button>
      </template>
    </BaseDialog>
  </div>
</template>

<script setup>
import { BasePagination, BaseDialog } from '@/shared'
import { useBatchHstPage } from './composables/useBatchHstPage'
import { BATCH_HST_CONSTANTS } from './constants'

const {
  loading,
  searchState,
  pageParams,
  batchData,
  selectedExecution,
  detailOpen,
  selectedRows,
  canRestart,
  options,
  fetchBatchHistoryList,
  handleRestart,
  handleExcelDownload,
  onRowClick,
  toggleRowSelection,
  isRowSelected,
  closeDetail,
  resetSearch,
  currentPage,
  pageSize,
} = useBatchHstPage()

const getStatusClass = (status) => {
  const classMap = {
    COMPLETED: 'status-completed',
    FAILED: 'status-failed',
    STARTED: 'status-started',
    STARTING: 'status-starting',
    STOPPED: 'status-stopped',
    UNKNOWN: 'status-unknown',
  }
  return classMap[status] || 'status-default'
}

const getExitCodeClass = (exitCode) => {
  const classMap = {
    COMPLETED: 'exit-success',
    FAILED: 'exit-failed',
    UNKNOWN: 'exit-unknown',
  }
  return classMap[exitCode] || 'exit-default'
}

const toggleAllRows = () => {
  if (selectedRows.value.length === batchData.value.length) {
    selectedRows.value = []
  } else {
    selectedRows.value = [...batchData.value]
  }
}

const formatStepInfo = (steps) => {
  if (!steps || steps.length === 0) return '{}'
  return JSON.stringify(steps, null, 2)
}

const formatParameterInfo = (parameters) => {
  if (!parameters || parameters.length === 0) return '{}'
  return JSON.stringify(parameters, null, 2)
}
</script>

<style scoped>
/* 검색폼 다중 행 레이아웃 */
.search-form-multi-row {
  display: flex;
  flex-direction: column;
  gap: 16px;
  align-items: stretch;
}

.search-row {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.date-range-group {
  min-width: 280px;
}

.date-range {
  display: flex;
  align-items: center;
  gap: 8px;
}

.date-range .form-input {
  flex: 1;
}

.date-separator {
  color: var(--dsx-color-neutral-text-alternative);
}

/* 테이블 */
.table-wrapper {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 1200px;
}

.data-table th {
  padding: 12px 16px;
  text-align: left;
  font-size: 13px;
  font-weight: 600;
  color: var(--dsx-color-interaction-text-inactive);
  border-bottom: 2px solid var(--dsx-color-neutral-border-alternative);
  white-space: nowrap;
}

.data-table td {
  padding: 12px 16px;
  font-size: 13px;
  color: var(--dsx-color-neutral-text-default);
  border-bottom: 1px solid var(--dsx-color-interaction-surface-disabled);
}

.checkbox-cell {
  width: 40px;
  text-align: center;
}

.table-checkbox {
  cursor: pointer;
  width: 16px;
  height: 16px;
}

.table-row-clickable {
  cursor: pointer;
  transition: background 0.15s;
}

.table-row-clickable:hover {
  background: var(--dsx-color-primary-fill-neutral);
}

.error-message-cell {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.table-empty {
  text-align: center;
  color: var(--dsx-color-interaction-text-inactive);
  padding: 32px 16px !important;
}

/* 상태 스타일 */
.status-completed { color: var(--dsx-color-positive-fill-default); font-weight: 600; }
.status-failed { color: var(--dsx-color-negative-fill-default); font-weight: 600; }
.status-started { color: var(--dsx-color-info-fill-default); font-weight: 600; }
.status-starting { color: var(--dsx-color-violet-fill-default); font-weight: 600; }
.status-stopped { color: var(--dsx-color-caution-fill-default); font-weight: 600; }
.status-unknown { color: var(--dsx-color-interaction-text-inactive); font-weight: 600; }

.exit-success { color: var(--dsx-color-positive-fill-default); font-weight: 600; }
.exit-failed { color: var(--dsx-color-negative-fill-default); font-weight: 600; }
.exit-unknown { color: var(--dsx-color-interaction-text-inactive); font-weight: 600; }

/* 상세 다이얼로그 정보 섹션 */
.info-section {
  margin-bottom: 16px;
}

.info-label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: var(--dsx-color-neutral-text-alternative);
  margin-bottom: 8px;
}

.code-block {
  background: var(--dsx-color-neutral-surface-alternative);
  border-radius: 8px;
  padding: 12px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 12px;
  white-space: pre-wrap;
  max-height: 150px;
  overflow-y: auto;
  color: var(--dsx-color-neutral-text-default);
  margin: 0;
  line-height: 1.5;
}

.code-block.error-message {
  color: var(--dsx-color-negative-fill-default);
}
</style>
