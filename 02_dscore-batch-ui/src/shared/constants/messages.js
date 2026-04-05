/**
 * 공통 메시지 상수
 *
 * 여러 페이지에서 동일하게 반복되는 메시지만 관리합니다.
 * 특정 페이지에서만 사용하는 메시지는 해당 페이지에 인라인으로 작성합니다.
 * i18n 도입 시 이 파일을 기준으로 전환할 수 있습니다.
 */
export const MESSAGES = Object.freeze({
  // CRUD 성공
  CREATED: '등록되었습니다.',
  UPDATED: '수정되었습니다.',
  DELETED: '삭제되었습니다.',
  SAVED: '저장되었습니다.',

  // CRUD 실패
  SAVE_FAILED: '저장에 실패했습니다.',
  FETCH_FAILED: '조회에 실패했습니다.',
  DELETE_FAILED: '삭제에 실패했습니다.',

  // 확인 (confirm)
  CONFIRM_DELETE: '정말 삭제하시겠습니까?',
  CONFIRM_SAVE: '저장하시겠습니까?',
  CONFIRM_LEAVE: '변경사항이 저장되지 않습니다. 페이지를 떠나시겠습니까?',

  // 유효성
  REQUIRED: '필수 입력 항목입니다.',
  INVALID_FORMAT: '입력 형식이 올바르지 않습니다.',

  // 인증/시스템
  SESSION_EXPIRED: '세션이 만료되었습니다. 다시 로그인해주세요.',
  NETWORK_ERROR: '네트워크 연결을 확인해주세요.',

  // 파일 업로드
  FILE_TYPE_NOT_ALLOWED: '허용되지 않는 파일 형식입니다.',
  FILE_SIZE_EXCEEDED: '파일 크기가 초과되었습니다.',
})
