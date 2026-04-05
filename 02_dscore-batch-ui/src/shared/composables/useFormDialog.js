import { ref, reactive, readonly } from 'vue'

/**
 * 폼 다이얼로그 공통 Composable
 *
 * 등록/수정 모달의 상태 관리를 위한 공통 로직을 제공합니다.
 *
 * @param {Object} initData - 폼 초기 데이터
 * @returns {Object} 다이얼로그 상태 및 함수
 *
 * @example
 * const { visible, isEditMode, form, openCreate, openEdit, close } = useFormDialog(INIT_DATA)
 */
export const useFormDialog = (initData = {}) => {
  // 다이얼로그 표시 여부
  const visible = ref(false)

  // 편집 모드 여부 (true: 수정, false: 등록)
  const isEditMode = ref(false)

  // 폼 데이터
  const form = reactive({ ...initData })

  // 원본 데이터 (수정 취소 시 복원용)
  let originalData = null

  /**
   * 등록 모드로 다이얼로그 열기
   * @param {Object} defaultValues - 기본값으로 설정할 데이터 (선택)
   */
  const openCreate = (defaultValues = {}) => {
    isEditMode.value = false
    Object.assign(form, { ...initData, ...defaultValues })
    originalData = null
    visible.value = true
  }

  /**
   * 수정 모드로 다이얼로그 열기
   * @param {Object} data - 수정할 데이터
   */
  const openEdit = (data) => {
    isEditMode.value = true
    Object.assign(form, { ...initData, ...data })
    originalData = { ...data }
    visible.value = true
  }

  /**
   * 다이얼로그 닫기
   */
  const close = () => {
    visible.value = false
  }

  /**
   * 폼 데이터 초기화
   */
  const reset = () => {
    Object.assign(form, initData)
    originalData = null
  }

  /**
   * 원본 데이터로 복원 (수정 모드에서 취소 시)
   */
  const restore = () => {
    if (originalData) {
      Object.assign(form, originalData)
    } else {
      reset()
    }
  }

  /**
   * 폼 데이터 업데이트
   * @param {Object} data - 업데이트할 데이터
   */
  const updateForm = (data) => {
    Object.assign(form, data)
  }

  /**
   * 특정 필드 값 설정
   * @param {string} key - 필드명
   * @param {*} value - 값
   */
  const setField = (key, value) => {
    form[key] = value
  }

  /**
   * 현재 폼 데이터의 복사본 반환
   * @returns {Object} 폼 데이터 복사본
   */
  const getFormData = () => {
    return { ...form }
  }

  /**
   * 데이터 변경 여부 확인
   * @returns {boolean} 변경 여부
   */
  const hasChanges = () => {
    if (!originalData) return false
    return JSON.stringify(form) !== JSON.stringify(originalData)
  }

  return {
    // 상태
    visible,
    isEditMode: readonly(isEditMode),
    form,
    // 함수
    openCreate,
    openEdit,
    close,
    reset,
    restore,
    updateForm,
    setField,
    getFormData,
    hasChanges,
  }
}
