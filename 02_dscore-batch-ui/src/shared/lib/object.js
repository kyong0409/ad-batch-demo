/**
 * 객체 관련 유틸리티 함수
 *
 * 객체 검증, 변환 관련 함수들
 */
/**
 * 값이 비어있는지 확인
 * @param {*} value - 확인할 값
 * @returns {boolean} 비어있으면 true
 */
export const isEmpty = (value) => {
  const str = String(value)
  return value == null || str === '' || str === 'null' || str === 'undefined' || str === 'NaN'
}

/**
 * 객체에서 빈 값을 가진 속성 제거
 * @param {Object} obj - 대상 객체
 * @returns {Object} 빈 값이 제거된 새 객체
 */
export const removeEmptyValues = (obj) => {
  return Object.fromEntries(
    Object.entries(obj).filter(([_, v]) => !isEmpty(v))
  )
}

/**
 * 폼 데이터가 초기값과 비교하여 변경되었는지 확인
 * @param {Object} currentForm - 현재 폼 데이터
 * @param {Object} initialForm - 초기 폼 데이터
 * @returns {boolean} 변경되었으면 true, 아니면 false
 */
export const isFormChanged = (currentForm, initialForm) => {
  return Object.keys(currentForm).some((key) => {
    const current = String(currentForm[key] ?? '')
    const initial = String(initialForm[key] ?? '')
    return current !== initial
  })
}
