import { httpClient as api, API_ENDPOINTS } from '@/shared/api'

const BASE_URL = API_ENDPOINTS.BATCH_HISTORY

/**
 * 배치 실행 이력 목록 조회
 */
export const getBatchHistoryList = (params) => {
  return api.get(BASE_URL, { params })
}

/**
 * 배치 실행 이력 상세 조회
 */
export const getBatchHistoryDetail = (jobExecutionId) => {
  return api.get(`${BASE_URL}/${jobExecutionId}`)
}

/**
 * 배치 재실행
 */
export const restartBatchExecution = (data) => {
  return api.put(`${BASE_URL}/trigger`, data)
}

/**
 * 배치 실행 이력 엑셀 다운로드
 */
export const downloadBatchHistoryExcel = (params) => {
  return api.get(`${BASE_URL}/excel`, {
    params,
    responseType: 'blob',
  })
}
