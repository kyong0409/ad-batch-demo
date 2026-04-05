/**
 * 공통 유틸리티 함수
 *
 * 2개 이상의 페이지에서 사용하는 유틸리티 함수를 관리합니다.
 *
 * 예시:
 * - date.js      : 날짜 포맷팅
 * - file.js      : 파일 다운로드
 * - validation.js: 공통 정규식 (이메일, 전화번호 등)
 */

// 리스트 유틸리티
export {
  addRowNum,
  addRowNumAsc,
  findByKey,
  findIndexByKey,
  groupBy,
} from './list'

// 포맷 유틸리티
export {
  formatUseYn,
  formatYesNo,
  formatYN,
  formatNumber,
  formatPhone,
  formatEmpty,
  truncate,
  formatBytes,
} from './format'
