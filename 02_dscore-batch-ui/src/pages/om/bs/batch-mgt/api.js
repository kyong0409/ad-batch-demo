import { httpClient as api, API_ENDPOINTS } from '@/shared/api'

export const getBatchScheduleList = (params) => {
  return api.get(API_ENDPOINTS.BATCH_MANAGE, { params })
}

export const getBatchScheduleDetail = (jobGroup, jobNm) => {
  return api.get(`${API_ENDPOINTS.BATCH_MANAGE}/${jobGroup}/${jobNm}`)
}

export const createBatchSchedule = (data) => {
  return api.post(API_ENDPOINTS.BATCH_MANAGE, data)
}

export const updateBatchSchedule = (jobGroup, jobNm, data) => {
  return api.put(`${API_ENDPOINTS.BATCH_MANAGE}/${jobGroup}/${jobNm}`, data)
}

export const downloadBatchScheduleExcel = (params) => {
  return api.get(API_ENDPOINTS.BATCH_MANAGE_EXCEL, {
    params,
    responseType: 'blob',
  })
}
