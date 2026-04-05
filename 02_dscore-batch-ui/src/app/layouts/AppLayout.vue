<template>
  <div id="wrap" class="app-layout">
    <a href="#main-content" class="skip-link">본문으로 건너뛰기</a>
    <AppHeader />
    <div class="layout-body">
      <AppSidebar />
      <main id="main-content" class="content" tabindex="-1">
        <slot />
      </main>
    </div>
    <AppFooter />
  </div>
</template>

<script setup>
import { onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useMenuStore } from '@/app/store/menuStore'
import AppHeader from './AppHeader.vue'
import AppSidebar from './AppSidebar.vue'
import AppFooter from './AppFooter.vue'

const route = useRoute()
const menuStore = useMenuStore()

onMounted(() => {
  menuStore.fetchMenuData(route.path)
})

watch(
  () => route.path,
  (path) => {
    menuStore.updateSelectedByPath(path)
  },
  { immediate: true },
)
</script>

<style scoped>
.app-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.layout-body {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.content {
  flex: 1;
  overflow-y: auto;
  background: var(--dsx-color-neutral-surface-alternative);
}
</style>
