import { ref, reactive, onMounted, computed } from 'vue'
import { useLoading } from '@/shared/composables'
import {
  getBatchHistoryList,
  getBatchHistoryDetail,
  restartBatchExecution,
  downloadBatchHistoryExcel,
} from '../api'
import { BATCH_HST_SEARCH_INIT, BATCH_HST_DETAIL_INIT, BATCH_HST_CONSTANTS } from '../constants'
import { removeEmptyValues } from '@/shared/lib/object'
import { addRowNum } from '@/shared/lib'
import { getCodeOptions } from '@/modules/code'

export const useBatchHst = () => {
  const { loading, execute } = useLoading()

  const searchState = reactive({ ...BATCH_HST_SEARCH_INIT })
  const pageParams = reactive({ totalElements: 0 })
  const batchData = ref([])
  const selectedExecution = reactive({ ...BATCH_HST_DETAIL_INIT })
  const detailOpen = ref(false)
  const selectedRows = ref([])

  // 페이지네이션 상태 (v-model 호환)
  const currentPage = ref(1)
  const pageSize = ref(10)

  // 코드 옵션
  const options = reactive({
    BATCH_STATUS: [],
  })

  /** 배치 실행 이력 목록 조회 */
  const fetchBatchHistoryList = async () => {
    await execute(async () => {
      const params = removeEmptyValues({
        ...searchState,
        page: currentPage.value - 1,
        size: pageSize.value,
      })
      const { data: res } = await getBatchHistoryList(params)
      const result = res.data?.result ?? []
      const totalElements = res.data?.page?.totalElements ?? 0

      pageParams.totalElements = totalElements
      batchData.value = addRowNum(result, currentPage.value, pageSize.value, totalElements).map((item) => ({
        ...item,
        statusLabel: BATCH_HST_CONSTANTS.STATUS_MAP[item.status] || item.status,
        exitCodeLabel: BATCH_HST_CONSTANTS.EXIT_CODE_MAP[item.exitCode] || item.exitCode,
      }))
    })
  }

  /** 배치 실행 이력 상세 조회 */
  const fetchBatchHistoryDetail = async (jobExecutionId) => {
    if (!jobExecutionId) return
    await execute(async () => {
      const { data: res } = await getBatchHistoryDetail(jobExecutionId)
      if (res.data) {
        const steps = res.data.batchHstStepList || []
        const parameters = res.data.batchHstJobParamList || []
        // Extract error message from failed steps
        const failedStep = steps.find((s) => s.exitCode === 'FAILED' && s.exitMessage)
        const exitMessage = failedStep
          ? failedStep.exitMessage.replace(/\\n/g, '\n').replace(/&quot;/g, '"')
          : ''

        Object.assign(selectedExecution, {
          steps,
          parameters,
          exitMessage,
        })
        detailOpen.value = true
      }
    })
  }

  /** 배치 재실행 */
  const handleRestart = async () => {
    if (selectedRows.value.length === 0) {
      alert('재실행할 배치를 선택해주세요.')
      return
    }

    if (!confirm('선택한 배치를 재실행하시겠습니까?')) {
      return
    }

    await execute(async () => {
      const jobExecutionIds = selectedRows.value.map((row) => row.jobExecutionId)
      await restartBatchExecution({ jobExecutionIds })
      alert('배치 재실행이 요청되었습니다.')
      selectedRows.value = []
      fetchBatchHistoryList()
    })
  }

  /** 엑셀 다운로드 */
  const handleExcelDownload = async () => {
    await execute(async () => {
      const params = { ...searchState }
      const { data } = await downloadBatchHistoryExcel(params)

      const blob = new Blob([data], {
        type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `batch_history_${new Date().getTime()}.xlsx`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
    })
  }

  /** 행 클릭 핸들러 */
  const onRowClick = (row) => {
    fetchBatchHistoryDetail(row.jobExecutionId)
  }

  /** 행 선택 토글 */
  const toggleRowSelection = (row) => {
    const index = selectedRows.value.findIndex((r) => r.jobExecutionId === row.jobExecutionId)
    if (index > -1) {
      selectedRows.value.splice(index, 1)
    } else {
      selectedRows.value.push(row)
    }
  }

  /** 행 선택 여부 확인 */
  const isRowSelected = (row) => {
    return selectedRows.value.some((r) => r.jobExecutionId === row.jobExecutionId)
  }

  /** 상세 다이얼로그 닫기 */
  const closeDetail = () => {
    detailOpen.value = false
    Object.assign(selectedExecution, BATCH_HST_DETAIL_INIT)
  }

  /** 검색 초기화 */
  const resetSearch = () => {
    Object.assign(searchState, BATCH_HST_SEARCH_INIT)
    currentPage.value = 1
    fetchBatchHistoryList()
  }

  /** 재실행 버튼 활성화 여부 */
  const canRestart = computed(() => selectedRows.value.length > 0)

  /** 초기화: 코드 옵션 로드 후 목록 조회 */
  const init = async () => {
    const result = await getCodeOptions([
      { groupId: 'BATCH_STATUS', includeAll: true },
    ])
    options.BATCH_STATUS = result.BATCH_STATUS
    await fetchBatchHistoryList()
  }

  onMounted(() => {
    init()
  })

  return {
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
    fetchBatchHistoryDetail,
    handleRestart,
    handleExcelDownload,
    onRowClick,
    toggleRowSelection,
    isRowSelected,
    closeDetail,
    resetSearch,
    currentPage,
    pageSize,
  }
}
