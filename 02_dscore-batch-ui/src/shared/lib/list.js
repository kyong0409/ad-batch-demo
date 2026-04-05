/**
 * 리스트 관련 유틸리티 함수
 */

/**
 * 리스트에 역순 행 번호 추가
 *
 * @param {Array} list - 데이터 리스트
 * @param {number} page - 현재 페이지 (1-based)
 * @param {number} size - 페이지 사이즈
 * @param {number} totalElements - 전체 요소 수
 * @returns {Array} rowNum이 추가된 리스트
 *
 * @example
 * const dataWithRowNum = addRowNum(data, 1, 10, 100)
 * // [{ ...item, rowNum: 100 }, { ...item, rowNum: 99 }, ...]
 */
export const addRowNum = (list, page, size, totalElements) => {
  return list.map((item, index) => ({
    ...item,
    rowNum: totalElements - (page - 1) * size - index,
  }))
}

/**
 * 리스트에 순방향 행 번호 추가
 *
 * @param {Array} list - 데이터 리스트
 * @param {number} page - 현재 페이지 (1-based)
 * @param {number} size - 페이지 사이즈
 * @returns {Array} rowNum이 추가된 리스트
 *
 * @example
 * const dataWithRowNum = addRowNumAsc(data, 1, 10)
 * // [{ ...item, rowNum: 1 }, { ...item, rowNum: 2 }, ...]
 */
export const addRowNumAsc = (list, page, size) => {
  return list.map((item, index) => ({
    ...item,
    rowNum: (page - 1) * size + index + 1,
  }))
}

/**
 * 리스트에서 특정 키 값으로 항목 찾기
 *
 * @param {Array} list - 데이터 리스트
 * @param {string} key - 검색할 키
 * @param {*} value - 검색할 값
 * @returns {Object|undefined} 찾은 항목 또는 undefined
 */
export const findByKey = (list, key, value) => {
  return list.find((item) => item[key] === value)
}

/**
 * 리스트에서 특정 키 값으로 인덱스 찾기
 *
 * @param {Array} list - 데이터 리스트
 * @param {string} key - 검색할 키
 * @param {*} value - 검색할 값
 * @returns {number} 찾은 인덱스 또는 -1
 */
export const findIndexByKey = (list, key, value) => {
  return list.findIndex((item) => item[key] === value)
}

/**
 * 리스트를 특정 키로 그룹화
 *
 * @param {Array} list - 데이터 리스트
 * @param {string} key - 그룹화할 키
 * @returns {Object} 그룹화된 객체
 *
 * @example
 * groupBy([{ type: 'A', name: '1' }, { type: 'A', name: '2' }, { type: 'B', name: '3' }], 'type')
 * // { A: [{ type: 'A', name: '1' }, { type: 'A', name: '2' }], B: [{ type: 'B', name: '3' }] }
 */
export const groupBy = (list, key) => {
  return list.reduce((acc, item) => {
    const groupKey = item[key]
    if (!acc[groupKey]) {
      acc[groupKey] = []
    }
    acc[groupKey].push(item)
    return acc
  }, {})
}
