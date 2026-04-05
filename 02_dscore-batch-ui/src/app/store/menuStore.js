import { ref, reactive } from 'vue'
import { defineStore } from 'pinia'

const BATCH_MENU_DATA = [
  {
    menuId: 'MNU_BATCH',
    menuNm: '배치 관리',
    children: [
      {
        menuId: 'MNU_BATCH_01',
        menuNm: '배치 스케줄 관리',
        urlAdr: '/om/bs/batch/manage/batchMgt',
        children: [],
      },
      {
        menuId: 'MNU_BATCH_02',
        menuNm: '배치 실행 이력',
        urlAdr: '/om/bs/batch/history/batchHstMgt',
        children: [],
      },
    ],
  },
]

export const useMenuStore = defineStore('menu', () => {
  const menuData = ref(BATCH_MENU_DATA)
  const selectedMenu = reactive({ depth1: null, depth2: null, depth3: null })
  const isLoading = ref(false)

  const fetchMenuData = async (currentPath = null) => {
    menuData.value = BATCH_MENU_DATA
    if (currentPath) {
      updateSelectedByPath(currentPath)
    }
  }

  const updateSelectedByPath = (path) => {
    for (const d1 of menuData.value) {
      for (const d2 of d1.children || []) {
        if (d2.urlAdr === path) {
          selectedMenu.depth1 = d1
          selectedMenu.depth2 = d2
          selectedMenu.depth3 = null
          return
        }
        for (const d3 of d2.children || []) {
          if (d3.urlAdr === path) {
            selectedMenu.depth1 = d1
            selectedMenu.depth2 = d2
            selectedMenu.depth3 = d3
            return
          }
        }
      }
    }
  }

  const selectMenu = (depth1Item) => {
    selectedMenu.depth1 = depth1Item
    selectedMenu.depth2 = null
    selectedMenu.depth3 = null
  }

  const hasMenuUrl = (path) => {
    for (const d1 of menuData.value) {
      for (const d2 of d1.children || []) {
        if (d2.urlAdr === path) return true
        for (const d3 of d2.children || []) {
          if (d3.urlAdr === path) return true
        }
      }
    }
    return false
  }

  return {
    menuData,
    selectedMenu,
    isLoading,
    fetchMenuData,
    updateSelectedByPath,
    selectMenu,
    hasMenuUrl,
  }
})
