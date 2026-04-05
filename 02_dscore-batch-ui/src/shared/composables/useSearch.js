import { reactive } from 'vue'

/**
 * 검색 상태 관리 공통 Composable
 *
 * @param {Object} initData - 검색 파라미터 초기값
 * @param {Object} options - 옵션
 * @param {Function} options.onSearch - 검색 실행 시 호출할 함수
 * @param {Function} options.onReset - 초기화 후 호출할 함수
 * @returns {Object} 검색 상태 및 함수
 *
 * @example
 * const { params, search, reset } = useSearch(SEARCH_INIT_DATA, {
 *   onSearch: () => fetchList(),
 *   onReset: () => fetchList()
 * })
 */
export const useSearch = (initData = {}, options = {}) => {
  const { onSearch, onReset } = options

  // 검색 파라미터
  const params = reactive({ ...initData })

  /**
   * 검색 실행
   */
  const search = () => {
    if (onSearch) onSearch()
  }

  /**
   * 검색 조건 초기화
   */
  const reset = () => {
    Object.assign(params, initData)
    if (onReset) onReset()
  }

  /**
   * 특정 필드 값 설정
   * @param {string} key - 필드명
   * @param {*} value - 값
   */
  const setParam = (key, value) => {
    params[key] = value
  }

  /**
   * 여러 필드 값 설정
   * @param {Object} data - 설정할 데이터
   */
  const setParams = (data) => {
    Object.assign(params, data)
  }

  /**
   * 현재 검색 파라미터 복사본 반환
   * @returns {Object} 검색 파라미터 복사본
   */
  const getParams = () => {
    return { ...params }
  }

  /**
   * 빈 값 제거한 파라미터 반환
   * @returns {Object} 빈 값이 제거된 파라미터
   */
  const getCleanParams = () => {
    const result = {}
    for (const [key, value] of Object.entries(params)) {
      if (value !== '' && value !== null && value !== undefined) {
        result[key] = value
      }
    }
    return result
  }

  return {
    params,
    search,
    reset,
    setParam,
    setParams,
    getParams,
    getCleanParams,
  }
}
