export const setupMock = async () => {
  if (import.meta.env.VITE_ENABLE_MOCK === 'true') {
    const { startMockWorker } = await import('../../mocks/mockWorker.js')
    await startMockWorker()
  }
}
