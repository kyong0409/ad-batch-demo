<template>
  <section class="search-section">
    <div class="search-form" :class="layoutClass">
      <!-- 검색 필드 슬롯 -->
      <slot />

      <!-- 액션 버튼 -->
      <div class="form-actions">
        <slot name="actions">
          <button class="btn-reset" type="button" @click="$emit('reset')">
            {{ resetText }}
          </button>
          <button class="btn-search" type="button" @click="$emit('search')">
            {{ searchText }}
          </button>
        </slot>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  layout: {
    type: String,
    default: 'inline',
    validator: (value) => ['inline', 'grid'].includes(value),
  },
  columns: {
    type: Number,
    default: 4,
  },
  searchText: {
    type: String,
    default: '검색',
  },
  resetText: {
    type: String,
    default: '초기화',
  },
})

defineEmits(['search', 'reset'])

const layoutClass = computed(() => {
  if (props.layout === 'grid') {
    return `search-form--grid search-form--cols-${props.columns}`
  }
  return 'search-form--inline'
})
</script>

<style scoped>
.search-section {
  background: var(--dsx-color-neutral-surface-default);
  border-radius: 12px;
  padding: 24px;
  box-shadow: var(--dsx-shadow-md);
}

.search-form {
  display: flex;
  gap: 16px;
}

/* 인라인 레이아웃 */
.search-form--inline {
  flex-wrap: wrap;
  align-items: flex-end;
}

/* 그리드 레이아웃 */
.search-form--grid {
  display: grid;
  align-items: end;
}

.search-form--cols-2 {
  grid-template-columns: repeat(2, 1fr) auto;
}

.search-form--cols-3 {
  grid-template-columns: repeat(3, 1fr) auto;
}

.search-form--cols-4 {
  grid-template-columns: repeat(4, 1fr) auto;
}

.form-actions {
  display: flex;
  gap: 8px;
  align-items: flex-end;
}

.btn-search,
.btn-reset {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.btn-search {
  background: var(--dsx-color-primary-fill-default);
  color: var(--dsx-color-static-white);
}

.btn-search:hover {
  background: var(--dsx-color-primary-fill-hover);
}

.btn-reset {
  background: var(--dsx-color-interaction-surface-disabled);
  color: var(--dsx-color-neutral-text-alternative);
  border: 1px solid var(--dsx-color-neutral-border-default);
}

.btn-reset:hover {
  background: var(--dsx-color-neutral-border-default);
}

@media (max-width: 768px) {
  .search-form {
    flex-direction: column;
  }

  .search-form--grid {
    grid-template-columns: 1fr;
  }

  .form-actions {
    width: 100%;
    justify-content: stretch;
  }

  .btn-search,
  .btn-reset {
    flex: 1;
  }
}
</style>
