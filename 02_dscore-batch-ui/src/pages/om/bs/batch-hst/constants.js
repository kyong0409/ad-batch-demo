export const BATCH_HST_CONSTANTS = {
  TABLE_HEADERS: [
    { label: '순번', field: 'rowNum' },
    { label: 'Job Name', field: 'jobName' },
    { label: 'Job Instance ID', field: 'jobInstanceId' },
    { label: 'Job Execution ID', field: 'jobExecutionId' },
    { label: '시작일시', field: 'startTime' },
    { label: '종료일시', field: 'endTime' },
    { label: '작업상태', field: 'statusLabel' },
    { label: '작업결과', field: 'exitCodeLabel' },
    { label: '에러메세지', field: 'exitMessage' },
  ],
  STATUS_MAP: {
    COMPLETED: '완료',
    FAILED: '실패',
    STARTED: '실행중',
    STARTING: '실행',
    STOPPED: '중단',
    UNKNOWN: '알수없음',
  },
  EXIT_CODE_MAP: {
    COMPLETED: '정상',
    FAILED: '실패',
    UNKNOWN: '알수없음',
  },
}

export const BATCH_HST_SEARCH_INIT = {
  jobName: '',
  jobInstanceId: '',
  jobExecutionId: '',
  startTime: '',
  endTime: '',
  status: '',
  exitCode: '',
}

export const BATCH_HST_DETAIL_INIT = {
  steps: [],
  parameters: [],
  exitMessage: '',
}

export const PAGE_INIT_DATA = {
  page: 1,
  size: 10,
  totalElements: 0,
}
