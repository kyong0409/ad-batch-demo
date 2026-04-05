/**
 * 파일 관련 유틸리티 함수
 *
 * 파일 다운로드 및 변환 관련 함수들
 */

/**
 * Base64 데이터를 파일로 다운로드
 * @param {string} base64Data - Base64 인코딩된 파일 데이터
 * @param {string} fileName - 다운로드할 파일명
 * @param {string} mimeType - MIME 타입 (기본값: 'application/octet-stream')
 */
export const downloadFile = (base64Data, fileName, mimeType = 'application/octet-stream') => {
  const byteCharacters = atob(base64Data)
  const byteNumbers = new Array(byteCharacters.length)
    .fill()
    .map((_, i) => byteCharacters.charCodeAt(i))
  const byteArray = new Uint8Array(byteNumbers)
  const blob = new Blob([byteArray], { type: mimeType })

  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = fileName
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(link.href)
}

/**
 * Base64 데이터를 Excel 파일로 다운로드
 * @param {string} base64Data - Base64 인코딩된 파일 데이터
 * @param {string} fileName - 다운로드할 파일명 (기본값: 'download.xlsx')
 */
export const downloadExcel = (base64Data, fileName = 'download.xlsx') => {
  downloadFile(base64Data, fileName, 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet')
}

// Alias for backward compatibility
export const downloadExcelFile = downloadExcel

/**
 * Blob을 Base64 문자열로 변환
 * @param {Blob} blob - 변환할 Blob 객체
 * @returns {Promise<string>} Base64 인코딩된 문자열
 */
export const blobToBase64 = (blob) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onloadend = () => resolve(reader.result)
    reader.onerror = reject
    reader.readAsDataURL(blob)
  })
}
