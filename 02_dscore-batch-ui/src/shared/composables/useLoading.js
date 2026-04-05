import { ref, readonly } from 'vue'

export const useLoading = () => {
  const loading = ref(false)
  const error = ref(null)

  const execute = async (fn) => {
    loading.value = true
    error.value = null
    try {
      return await fn()
    } catch (err) {
      error.value = err instanceof Error ? err : new Error(String(err))
      return undefined
    } finally {
      loading.value = false
    }
  }

  const reset = () => {
    loading.value = false
    error.value = null
  }

  return {
    loading: readonly(loading),
    error: readonly(error),
    execute,
    reset,
  }
}
