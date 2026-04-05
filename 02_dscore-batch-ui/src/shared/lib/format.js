/**
 * 포맷팅 관련 유틸리티 함수
 */

/**
 * 사용여부 값을 라벨로 변환
 *
 * @param {string} value - 'Y' 또는 'N'
 * @returns {string} '사용' 또는 '미사용'
 */
export const formatUseYn = (value) => {
  return value === 'Y' ? '사용' : '미사용'
}

/**
 * Y/N 값을 예/아니오로 변환
 *
 * @param {string} value - 'Y' 또는 'N'
 * @returns {string} '예' 또는 '아니오'
 */
export const formatYesNo = (value) => {
  return value === 'Y' ? '예' : '아니오'
}

/**
 * Y/N 값을 사용자 정의 라벨로 변환
 *
 * @param {string} value - 'Y' 또는 'N'
 * @param {string} yesLabel - Y일 때 표시할 라벨
 * @param {string} noLabel - N일 때 표시할 라벨
 * @returns {string} 변환된 라벨
 */
export const formatYN = (value, yesLabel = '예', noLabel = '아니오') => {
  return value === 'Y' ? yesLabel : noLabel
}

/**
 * 숫자를 천 단위 콤마 포맷으로 변환
 *
 * @param {number|string} value - 숫자 값
 * @returns {string} 포맷된 숫자 문자열
 *
 * @example
 * formatNumber(1234567) // '1,234,567'
 */
export const formatNumber = (value) => {
  if (value === null || value === undefined || value === '') return ''
  return Number(value).toLocaleString('ko-KR')
}

/**
 * 전화번호 포맷팅 (010-1234-5678 / 02-123-4567)
 *
 * @param {string|number} value - 전화번호
 * @returns {string} 포맷된 전화번호
 */
export const formatPhone = (value) => {
  if (value == null) return ''
  const digits = String(value).replace(/\D/g, '')
  if (digits.length === 11) return digits.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3')
  if (digits.length === 10) return digits.replace(/(\d{3})(\d{3})(\d{4})/, '$1-$2-$3')
  return value
}

/**
 * 빈 값을 대체 문자로 변환
 *
 * @param {*} value - 원본 값
 * @param {string} placeholder - 빈 값일 때 표시할 문자
 * @returns {string} 원본 값 또는 대체 문자
 */
export const formatEmpty = (value, placeholder = '-') => {
  if (value === null || value === undefined || value === '') {
    return placeholder
  }
  return value
}

/**
 * 문자열 말줄임
 *
 * @param {string} value - 원본 문자열
 * @param {number} maxLength - 최대 길이
 * @param {string} suffix - 말줄임 접미사
 * @returns {string} 말줄임된 문자열
 *
 * @example
 * truncate('긴 문자열입니다', 5) // '긴 문자열...'
 */
export const truncate = (value, maxLength, suffix = '...') => {
  if (!value) return ''
  if (value.length <= maxLength) return value
  return value.slice(0, maxLength) + suffix
}

/**
 * 바이트 크기를 읽기 쉬운 형식으로 변환
 *
 * @param {number} bytes - 바이트 크기
 * @param {number} decimals - 소수점 자릿수
 * @returns {string} 포맷된 크기 문자열
 *
 * @example
 * formatBytes(1024) // '1 KB'
 * formatBytes(1234567) // '1.18 MB'
 */
export const formatBytes = (bytes, decimals = 2) => {
  if (bytes === 0) return '0 Bytes'

  const k = 1024
  const dm = decimals < 0 ? 0 : decimals
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB']

  const i = Math.floor(Math.log(bytes) / Math.log(k))

  return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i]
}
