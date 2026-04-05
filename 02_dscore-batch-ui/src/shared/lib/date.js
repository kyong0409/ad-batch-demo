/**
 * 날짜 관련 유틸리티 함수
 *
 * dayjs를 사용한 날짜 포맷팅 및 계산 함수들
 */
import dayjs from 'dayjs'

/**
 * 날짜를 지정된 포맷으로 변환
 * @param {Date|string} date - 변환할 날짜
 * @param {string} format - 출력 포맷 (기본값: 'YYYY-MM-DD')
 * @returns {string} 포맷팅된 날짜 문자열
 */
export const formatDate = (date, format = 'YYYY-MM-DD') => {
  if (!date) return ''
  return dayjs(date).format(format)
}

/**
 * 날짜를 날짜+시간 포맷으로 변환
 * @param {Date|string} date - 변환할 날짜
 * @returns {string} 'YYYY-MM-DD HH:mm:ss' 포맷의 문자열
 */
export const formatDateTime = (date) => {
  if (!date) return ''
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

/**
 * 날짜의 시작 시간(00:00:00)으로 설정
 * @param {Date|string} date - 대상 날짜
 * @returns {string} 'YYYY-MM-DD 00:00:00' 포맷의 문자열
 */
export const setStartDate = (date) => {
  if (!date) return ''
  return dayjs(date).format('YYYY-MM-DD 00:00:00')
}

/**
 * 날짜의 종료 시간(23:59:59)으로 설정
 * @param {Date|string} date - 대상 날짜
 * @returns {string} 'YYYY-MM-DD 23:59:59' 포맷의 문자열
 */
export const setEndDate = (date) => {
  if (!date) return ''
  return dayjs(date).format('YYYY-MM-DD 23:59:59')
}

/**
 * 날짜만 추출 (시간 제거)
 * @param {Date|string} date - 대상 날짜
 * @returns {string} 'YYYY-MM-DD' 포맷의 문자열
 */
export const getDate = (date) => {
  if (!date) return ''
  return dayjs(date).format('YYYY-MM-DD')
}

/**
 * 현재 날짜 기준 상대 날짜 계산
 * @param {number} addYear - 추가할 년 수
 * @param {number} addMonth - 추가할 월 수
 * @param {number} addDay - 추가할 일 수
 * @returns {string} 'YYYY-MM-DD HH:mm:ss' 포맷의 문자열
 */
export const getRelativeDate = (addYear = 0, addMonth = 0, addDay = 0) => {
  return dayjs()
    .add(addYear, 'year')
    .add(addMonth, 'month')
    .add(addDay, 'day')
    .format('YYYY-MM-DD HH:mm:ss')
}
