export const API_ENDPOINTS = {
  // 배치 관리
  BATCH_MANAGE: '/om/batch-schedule/schedules',
  BATCH_MANAGE_EXCEL: '/om/batch-schedule/schedules/excel',

  // 배치 실행 이력
  BATCH_HISTORY: '/om/batch-history/histories',
  BATCH_HISTORY_TRIGGER: '/om/batch-history/histories/trigger',
  BATCH_HISTORY_EXCEL: '/om/batch-history/histories/excel',

  // 공통 코드
  CODE: '/om/code-mgt/codes',
  CODE_GROUPS: '/om/code-mgt/groups',
  CODE_GROUP_DETAIL: (cdGroupId) => `/om/code-mgt/groups/${cdGroupId}`,
  CODE_GROUP_CODES: (cdGroupId) => `/om/code-mgt/groups/${cdGroupId}/codes`,
}
