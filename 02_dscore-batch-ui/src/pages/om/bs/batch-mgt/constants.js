export const BATCH_MGT_CONSTANTS = {
  TABLE_HEADERS: [
    { label: '순번', field: 'rowNum' },
    { label: '배치 그룹', field: 'jobGroup' },
    { label: 'Job Name', field: 'jobNm' },
    { label: '파라미터(JSON)', field: 'paramSbst' },
    { label: '사용여부', field: 'useYnNm' },
    { label: '실행주기(크론식)', field: 'perdSbst' },
    { label: '등록자', field: 'regrNm' },
    { label: '등록일시', field: 'regDt' },
  ],
}

export const BATCH_SEARCH_INIT = {
  jobGroup: '',
  useYn: '',
}

export const BATCH_FORM_INIT = {
  jobGroup: '',
  jobNm: '',
  paramSbst: '',
  perdSbst: '',
  useYn: '',
  jobDesc: '',
}

export const PAGE_INIT_DATA = {
  page: 1,
  size: 10,
  totalElements: 0,
}
