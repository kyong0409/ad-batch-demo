<template>
  <aside class="app-sidebar">
    <!-- SNB 헤더: 현재 GNB 메뉴명 -->
    <h2 class="snb-header">
      {{ selectedMenu.depth1?.menuNm || '메뉴' }}
    </h2>

    <nav class="snb-nav">
      <div
        v-for="d2 in sideMenuItems"
        :key="d2.menuId"
        class="snb-group"
      >
        <!-- depth2 메뉴 버튼 -->
        <button
          class="snb-depth2"
          :class="{
            active: isActiveDepth2(d2),
            expanded: openMenuIds.has(d2.menuId)
          }"
          :aria-expanded="d2.children?.length ? openMenuIds.has(d2.menuId) : undefined"
          :aria-controls="d2.children?.length ? `submenu-${d2.menuId}` : undefined"
          @click="toggleDepth2(d2)"
        >
          <span class="snb-icon" aria-hidden="true">+</span>
          <span class="snb-label">{{ d2.menuNm }}</span>
          <span v-if="d2.children?.length" class="snb-arrow" aria-hidden="true">
            {{ openMenuIds.has(d2.menuId) ? '∧' : '∨' }}
          </span>
        </button>

        <!-- depth3 하위 메뉴 리스트 -->
        <div
          v-if="d2.children?.length && openMenuIds.has(d2.menuId)"
          :id="`submenu-${d2.menuId}`"
          class="snb-depth3-list"
        >
          <router-link
            v-for="d3 in d2.children"
            :key="d3.menuId"
            :to="d3.urlAdr || '#'"
            class="snb-depth3"
            :class="{ active: selectedMenu.depth3?.menuId === d3.menuId }"
            :aria-current="selectedMenu.depth3?.menuId === d3.menuId ? 'page' : undefined"
          >
            {{ d3.menuNm }}
          </router-link>
        </div>
      </div>

      <div v-if="sideMenuItems.length === 0" class="snb-empty">
        메뉴가 없습니다.
      </div>
    </nav>
  </aside>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useMenuStore } from '@/app/store/menuStore'

const router = useRouter()
const menuStore = useMenuStore()
const { selectedMenu } = storeToRefs(menuStore)

const openMenuIds = ref(new Set())

const sideMenuItems = computed(() => {
  return selectedMenu.value.depth1?.children || []
})

// depth2가 활성화되었는지 확인 (자신 또는 하위 메뉴가 선택됨)
const isActiveDepth2 = (d2) => {
  if (selectedMenu.value.depth2?.menuId === d2.menuId) return true
  if (d2.children?.some(d3 => d3.menuId === selectedMenu.value.depth3?.menuId)) return true
  return false
}

// depth2 선택 시 자동으로 열기
watch(
  () => selectedMenu.value.depth2,
  (d2) => {
    if (d2) {
      openMenuIds.value.add(d2.menuId)
    }
  },
  { immediate: true },
)

const toggleDepth2 = (d2) => {
  if (d2.children?.length) {
    // 하위 메뉴가 있으면 토글
    if (openMenuIds.value.has(d2.menuId)) {
      openMenuIds.value.delete(d2.menuId)
    } else {
      openMenuIds.value.add(d2.menuId)
    }
  } else if (d2.urlAdr) {
    // 하위 메뉴가 없으면 직접 페이지 이동
    router.push(d2.urlAdr)
  }
}
</script>

<style scoped>
.app-sidebar {
  width: 280px;
  min-width: 280px;
  background: var(--dsx-color-neutral-surface-default);
  border-right: 1px solid var(--dsx-color-neutral-border-default);
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

.snb-header {
  padding: 24px 20px 16px;
  font-size: 18px;
  font-weight: 700;
  color: var(--dsx-color-neutral-text-default);
}

.snb-nav {
  flex: 1;
  padding: 0 12px 16px;
}

.snb-group {
  margin-bottom: 4px;
}

.snb-depth2 {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 12px 16px;
  border: none;
  border-radius: 8px;
  background: transparent;
  font-size: 15px;
  font-weight: 500;
  color: var(--dsx-color-neutral-text-default);
  cursor: pointer;
  transition: all 0.2s;
  text-align: left;
  gap: 10px;
}

.snb-depth2:hover {
  background: var(--dsx-color-interaction-surface-disabled);
}

.snb-depth2.active {
  background: var(--dsx-color-primary-fill-default);
  color: var(--dsx-color-static-white);
}

.snb-depth2.active .snb-icon,
.snb-depth2.active .snb-arrow {
  color: var(--dsx-color-static-white);
}

.snb-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  border: 1px solid var(--dsx-color-interaction-border-inactive);
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  color: var(--dsx-color-neutral-text-alternative);
  flex-shrink: 0;
}

.snb-depth2.active .snb-icon {
  border-color: rgba(255, 255, 255, 0.5);
}

.snb-label {
  flex: 1;
}

.snb-arrow {
  font-size: 12px;
  color: var(--dsx-color-interaction-text-inactive);
  flex-shrink: 0;
}

.snb-depth3-list {
  display: flex;
  flex-direction: column;
  background: var(--dsx-color-neutral-surface-alternative);
  border-radius: 8px;
  margin: 4px 0 4px 30px;
  padding: 8px 0;
}

.snb-depth3 {
  padding: 10px 16px;
  font-size: 14px;
  color: var(--dsx-color-neutral-text-neutral);
  text-decoration: none;
  transition: all 0.15s;
}

.snb-depth3:hover {
  color: var(--dsx-color-primary-fill-default);
}

.snb-depth3.active {
  color: var(--dsx-color-primary-fill-default);
  font-weight: 600;
}

.snb-empty {
  padding: 20px;
  text-align: center;
  font-size: 13px;
  color: var(--dsx-color-interaction-text-inactive);
}
</style>
