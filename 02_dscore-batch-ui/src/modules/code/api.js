import { httpClient as api, API_ENDPOINTS } from '@/shared/api'

/**
 * 공통 코드 목록 조회
 * @param {string} cdGroupId - 코드 그룹 ID
 * @param {boolean} includeAll - '전체' 옵션 포함 여부 (기본값: false)
 * @returns {Promise<Array>} 코드 옵션 배열 [{label, value}, ...]
 */
export const getCodeList = async (cdGroupId, includeAll = false) => {
  const { data } = await api.get(API_ENDPOINTS.CODE_GROUP_CODES(cdGroupId))

  const options = (data.data?.result || []).map(item => ({
    label: item.cdNm,
    value: item.cdId || item.cd
  }))

  if (includeAll) {
    return [{ label: '전체', value: '' }, ...options]
  }
  return options
}

/**
 * 여러 코드 그룹 한번에 조회
 * @param {Array<{groupId: string, includeAll: boolean}>} groups - 조회할 코드 그룹 배열
 * @returns {Promise<Object>} 코드 그룹별 옵션 객체
 */
export const getCodeOptions = async (groups) => {
  const promises = groups.map(({ groupId, includeAll }) =>
    getCodeList(groupId, includeAll)
  )
  const results = await Promise.all(promises)

  return groups.reduce((acc, { groupId }, index) => {
    acc[groupId] = results[index]
    return acc
  }, {})
}
