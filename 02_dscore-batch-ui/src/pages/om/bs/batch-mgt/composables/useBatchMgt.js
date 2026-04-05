import { ref, reactive, onMounted } from 'vue'
import { useLoading } from '@/shared/composables'
import {
  getBatchScheduleList,
  getBatchScheduleDetail,
  createBatchSchedule,
  updateBatchSchedule,
  downloadBatchScheduleExcel,
} from '../api'
import { BATCH_SEARCH_INIT, BATCH_FORM_INIT } from '../constants'
import { removeEmptyValues } from '@/shared/lib/object'
import { addRowNum } from '@/shared/lib'
import { getCodeOptions } from '@/modules/code'

export const useBatchMgt = () => {
  const { loading, execute } = useLoading()

  const searchState = reactive({ ...BATCH_SEARCH_INIT })
  const pageParams = reactive({ totalElements: 0 })
  const batchData = ref([])
  const selectedBatch = reactive({ ...BATCH_FORM_INIT })
  const batchDetailOpen = ref(false)
  const isEditMode = ref(false)

  // 페이지네이션 상태 (v-model 호환)
  const currentPage = ref(1)
  const pageSize = ref(10)

  // 코드 옵션
  const options = reactive({
    BATCH_GROUP_NAME: [],
    USE_YN: [],
  })

  /** 배치 목록 조회 */
  const fetchBatchList = async () => {
    await execute(async () => {
      const params = removeEmptyValues({
        ...searchState,
        page: currentPage.value - 1,
        size: pageSize.value,
      })
      const { data: res } = await getBatchScheduleList(params)
      const result = res.data?.result ?? []
      const totalElements = res.data?.page?.totalElements ?? 0

      pageParams.totalElements = totalElements
      batchData.value = addRowNum(result, currentPage.value, pageSize.value, totalElements)
    })
  }

  /** 배치 상세 조회 */
  const fetchBatchDetail = async (jobGroup, jobNm) => {
    if (!jobGroup || !jobNm) return
    await execute(async () => {
      const { data: res } = await getBatchScheduleDetail(jobGroup, jobNm)
      if (res.data) {
        Object.assign(selectedBatch, res.data)
        batchDetailOpen.value = true
        isEditMode.value = true
      }
    })
  }

  /** 배치 행 클릭 핸들러 */
  const onRowClick = (row) => {
    fetchBatchDetail(row.jobGroup, row.jobNm)
  }

  /** 상세 다이얼로그 닫기 */
  const closeDetail = () => {
    batchDetailOpen.value = false
    isEditMode.value = false
    Object.assign(selectedBatch, BATCH_FORM_INIT)
  }

  /** 배치 등록 다이얼로그 열기 */
  const openRegisterDialog = () => {
    Object.assign(selectedBatch, BATCH_FORM_INIT)
    isEditMode.value = false
    batchDetailOpen.value = true
  }

  /** 배치 등록/수정 */
  const saveBatch = async () => {
    await execute(async () => {
      if (isEditMode.value) {
        await updateBatchSchedule(selectedBatch.jobGroup, selectedBatch.jobNm, selectedBatch)
        alert('배치가 수정되었습니다.')
      } else {
        await createBatchSchedule(selectedBatch)
        alert('배치가 등록되었습니다.')
      }
      closeDetail()
      fetchBatchList()
    })
  }

  /** 검색 초기화 */
  const resetSearch = () => {
    Object.assign(searchState, BATCH_SEARCH_INIT)
    currentPage.value = 1
    fetchBatchList()
  }

  /** 엑셀 다운로드 */
  const downloadExcel = async () => {
    await execute(async () => {
      const params = { ...searchState }
      const { data } = await downloadBatchScheduleExcel(params)
      const blob = new Blob([data], {
        type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `배치관리_${new Date().toISOString().slice(0, 10)}.xlsx`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
    })
  }

  /** 초기화: 코드 옵션 로드 후 목록 조회 */
  const init = async () => {
    const result = await getCodeOptions([
      { groupId: 'BATCH_GROUP_NAME', includeAll: true },
      { groupId: 'USE_YN', includeAll: true },
    ])
    options.BATCH_GROUP_NAME = result.BATCH_GROUP_NAME
    options.USE_YN = result.USE_YN
    await fetchBatchList()
  }

  onMounted(() => {
    init()
  })

  return {
    loading,
    searchState,
    pageParams,
    batchData,
    selectedBatch,
    batchDetailOpen,
    isEditMode,
    options,
    fetchBatchList,
    fetchBatchDetail,
    onRowClick,
    closeDetail,
    openRegisterDialog,
    saveBatch,
    resetSearch,
    downloadExcel,
    currentPage,
    pageSize,
  }
}
