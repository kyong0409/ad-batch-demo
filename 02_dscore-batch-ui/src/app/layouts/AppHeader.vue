<template>
  <header class="app-header">
    <div class="header-left">
      <div class="header-logo">
        <img src="/logo_800.png" alt="ADAM Batch" class="logo-img" />
      </div>
      <nav class="gnb-nav">
        <router-link
          v-for="menu in menuData"
          :key="menu.menuId"
          :to="getFirstChildUrl(menu)"
          class="gnb-item"
          :class="{ active: selectedMenu.depth1?.menuId === menu.menuId }"
          @click.prevent="onGnbClick(menu)"
        >
          {{ menu.menuNm }}
        </router-link>
      </nav>
    </div>
    <div class="header-right">
      <button
        class="theme-toggle"
        @click="toggleTheme"
        :aria-label="isDark ? '라이트 모드로 전환' : '다크 모드로 전환'"
        :aria-pressed="isDark"
      >
        <svg v-if="!isDark" aria-hidden="true" focusable="false" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="12" cy="12" r="5" />
          <line x1="12" y1="1" x2="12" y2="3" />
          <line x1="12" y1="21" x2="12" y2="23" />
          <line x1="4.22" y1="4.22" x2="5.64" y2="5.64" />
          <line x1="18.36" y1="18.36" x2="19.78" y2="19.78" />
          <line x1="1" y1="12" x2="3" y2="12" />
          <line x1="21" y1="12" x2="23" y2="12" />
          <line x1="4.22" y1="19.78" x2="5.64" y2="18.36" />
          <line x1="18.36" y1="5.64" x2="19.78" y2="4.22" />
        </svg>
        <svg v-else aria-hidden="true" focusable="false" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z" />
        </svg>
      </button>
    </div>
  </header>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useMenuStore } from '@/app/store/menuStore'
import { useThemeStore } from '@/app/store/themeStore'

const router = useRouter()
const menuStore = useMenuStore()
const themeStore = useThemeStore()

const { menuData, selectedMenu } = storeToRefs(menuStore)
const { isDark } = storeToRefs(themeStore)
const { toggleTheme } = themeStore

const onGnbClick = (menu) => {
  menuStore.selectMenu(menu)

  const firstChild = menu.children?.[0]
  if (firstChild) {
    const target = firstChild.children?.[0] ?? firstChild
    if (target.urlAdr) {
      router.push(target.urlAdr)
    }
  }
}

const getFirstChildUrl = (menu) => {
  const firstChild = menu.children?.[0]
  if (firstChild) {
    const target = firstChild.children?.[0] ?? firstChild
    if (target.urlAdr) return target.urlAdr
  }
  return '#'
}
</script>

<style scoped>
.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--dsx-space-9);
  height: 70px;
  background: #ffffff;
  color: #1a2332;
  border-bottom: 1px solid #e2e8f0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: var(--dsx-space-9);
}

.header-logo {
  display: flex;
  align-items: center;
  margin: 0;
  white-space: nowrap;
}

.logo-img {
  height: 52px;
  width: auto;
  object-fit: contain;
}

.gnb-nav {
  display: flex;
  align-items: center;
  gap: var(--dsx-space-2);
}

.gnb-item {
  display: inline-flex;
  align-items: center;
  padding: var(--dsx-space-3) var(--dsx-space-5);
  border: none;
  border-radius: var(--dsx-radius-large);
  background: transparent;
  color: #64748b;
  font-size: var(--dsx-font-size-body3);
  font-weight: var(--dsx-font-weight-medium);
  cursor: pointer;
  text-decoration: none;
  transition: background var(--dsx-transition-normal), color var(--dsx-transition-normal);
}

.gnb-item:hover {
  background: #f1f5f9;
  color: #1a2332;
}

.gnb-item.active {
  background: #e2e8f0;
  color: #1a2332;
  font-weight: var(--dsx-font-weight-semibold);
}

.header-right {
  display: flex;
  align-items: center;
  gap: var(--dsx-space-5);
}

.theme-toggle {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  padding: 0;
  border: 1px solid #e2e8f0;
  border-radius: var(--dsx-radius-xlarge);
  background: #f8fafc;
  color: #475569;
  cursor: pointer;
  transition: background var(--dsx-transition-normal), border-color var(--dsx-transition-normal);
}

.theme-toggle:hover {
  background: #e2e8f0;
  border-color: #cbd5e1;
}
</style>
