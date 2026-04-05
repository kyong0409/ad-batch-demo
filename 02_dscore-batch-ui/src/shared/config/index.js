export const config = {
  appName: import.meta.env.VITE_APP_NAME ?? 'dscore-batch-ui',
  appVersion: import.meta.env.VITE_APP_VERSION ?? '1.0.0',
  isProduction: import.meta.env.PROD,

  apiBaseUrl: import.meta.env.VITE_API_BASE_URL,
  apiTimeout: Number(import.meta.env.VITE_API_TIMEOUT ?? 10000),

  enableMock: import.meta.env.VITE_ENABLE_MOCK === 'true',

  entryRoute: {
    default: 'BatchMgt',
  },

  pagination: {
    defaultSize: Number(import.meta.env.VITE_DEFAULT_PAGE_SIZE ?? 10),
    sizeOptions: [10, 20, 50],
    maxVisiblePages: 5,
  },

  fileUpload: {
    maxSizeMB: Number(import.meta.env.VITE_FILE_UPLOAD_MAX_SIZE_MB ?? 10),
    allowedExtensions: ['xls', 'xlsx', 'docx', 'doc', 'pptx', 'ppt', 'hwt', 'pdf', 'zip', 'jpg', 'jpeg', 'png', 'gif'],
  },
}
