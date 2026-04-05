import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useThemeStore = defineStore('theme', () => {
  const isDark = ref(localStorage.getItem('dsx-theme') === 'dark')

  const applyTheme = (dark) => {
    document.documentElement.classList.toggle('dark', dark)
    localStorage.setItem('dsx-theme', dark ? 'dark' : 'light')
  }

  const toggleTheme = () => {
    isDark.value = !isDark.value
  }

  // 초기 적용
  applyTheme(isDark.value)

  watch(isDark, applyTheme)

  return { isDark, toggleTheme }
})
