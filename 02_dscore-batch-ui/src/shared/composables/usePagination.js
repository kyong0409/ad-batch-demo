import { ref, computed } from 'vue'

/**
 * 페이지네이션 공통 Composable
 *
 * @param {Function} fetchFn - 데이터 조회 함수 (페이지 변경 시 호출)
 * @param {Object} options - 옵션
 * @param {number} options.defaultSize - 기본 페이지 사이즈 (기본값: 10)
 * @param {number} options.maxVisiblePages - 최대 표시 페이지 수 (기본값: 5)
 * @returns {Object} 페이지네이션 상태 및 함수
 *
 * @example
 * const { page, size, totalElements, ... } = usePagination(fetchList, { defaultSize: 10 })
 */
export const usePagination = (fetchFn, options = {}) => {
  const { defaultSize = 10, maxVisiblePages = 5 } = options

  // 페이지네이션 상태
  const page = ref(1)
  const size = ref(defaultSize)
  const totalElements = ref(0)
  const goToPageInput = ref(1)

  // 전체 페이지 수
  const totalPages = computed(() => Math.ceil(totalElements.value / size.value) || 1)

  // 현재 페이지 (별칭)
  const currentPage = computed(() => page.value)

  // 페이지 사이즈 (v-model 지원)
  const pageSize = computed({
    get: () => size.value,
    set: (val) => {
      size.value = val
    },
  })

  // 표시할 페이지 번호 목록 계산
  const visiblePages = computed(() => {
    const total = totalPages.value
    const current = page.value
    const pages = []

    if (total <= maxVisiblePages) {
      // 전체 페이지가 최대 표시 수 이하면 모두 표시
      for (let i = 1; i <= total; i++) {
        pages.push(i)
      }
    } else {
      const half = Math.floor(maxVisiblePages / 2)

      if (current <= half + 1) {
        // 현재 페이지가 앞쪽에 있는 경우
        for (let i = 1; i <= maxVisiblePages; i++) {
          pages.push(i)
        }
      } else if (current >= total - half) {
        // 현재 페이지가 뒷쪽에 있는 경우
        for (let i = total - maxVisiblePages + 1; i <= total; i++) {
          pages.push(i)
        }
      } else {
        // 현재 페이지가 중간에 있는 경우
        for (let i = current - half; i <= current + half; i++) {
          pages.push(i)
        }
      }
    }

    return pages
  })

  /**
   * 특정 페이지로 이동
   * @param {number} targetPage - 이동할 페이지 번호
   */
  const goToPage = (targetPage) => {
    if (targetPage < 1 || targetPage > totalPages.value) return
    page.value = targetPage
    goToPageInput.value = targetPage
    if (fetchFn) fetchFn()
  }

  /**
   * 페이지 사이즈 변경 핸들러
   */
  const onPageSizeChange = () => {
    page.value = 1
    goToPageInput.value = 1
    if (fetchFn) fetchFn()
  }

  /**
   * 페이지네이션 상태 초기화
   */
  const reset = () => {
    page.value = 1
    size.value = defaultSize
    totalElements.value = 0
    goToPageInput.value = 1
  }

  /**
   * 총 요소 수 설정 (API 응답 후 호출)
   * @param {number} total - 총 요소 수
   */
  const setTotalElements = (total) => {
    totalElements.value = total
  }

  /**
   * API 요청용 파라미터 반환 (0-based page)
   * @returns {Object} { page, size }
   */
  const getParams = () => {
    return {
      page: page.value - 1, // 0-based for API
      size: size.value,
    }
  }

  return {
    // 상태
    page,
    size,
    totalElements,
    totalPages,
    currentPage,
    pageSize,
    visiblePages,
    goToPageInput,
    // 함수
    goToPage,
    onPageSizeChange,
    reset,
    setTotalElements,
    getParams,
  }
}
