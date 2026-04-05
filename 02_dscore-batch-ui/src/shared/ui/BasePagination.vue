<template>
  <nav class="base-pagination" aria-label="페이지 네비게이션">
    <!-- 페이지 사이즈 선택 -->
    <select
      :value="size"
      class="page-size-select"
      aria-label="페이지 당 항목 수"
      @change="handleSizeChange(Number($event.target.value))"
    >
      <option v-for="opt in pageSizeOptions" :key="opt" :value="opt">
        {{ opt }}건
      </option>
    </select>

    <!-- 페이지 버튼 -->
    <div class="page-numbers">
      <button
        class="page-btn"
        :disabled="page === 1"
        aria-label="첫 페이지"
        @click="handlePageChange(1)"
      >
        |&lt;
      </button>
      <button
        class="page-btn"
        :disabled="page === 1"
        aria-label="이전 페이지"
        @click="handlePageChange(page - 1)"
      >
        &lt;
      </button>
      <button
        v-for="p in visiblePages"
        :key="p"
        class="page-btn"
        :class="{ active: p === page }"
        :aria-current="p === page ? 'page' : undefined"
        :aria-label="'페이지 ' + p"
        @click="handlePageChange(p)"
      >
        {{ p }}
      </button>
      <button
        class="page-btn"
        :disabled="page === totalPages"
        aria-label="다음 페이지"
        @click="handlePageChange(page + 1)"
      >
        &gt;
      </button>
      <button
        class="page-btn"
        :disabled="page === totalPages"
        aria-label="마지막 페이지"
        @click="handlePageChange(totalPages)"
      >
        &gt;|
      </button>
    </div>

    <!-- 페이지 이동 입력 -->
    <div class="page-info">
      <input
        v-model.number="localGoToPage"
        type="number"
        class="page-input"
        aria-label="이동할 페이지 번호"
        min="1"
        :max="totalPages"
        @keyup.enter="handleGoToPage"
      />
      <span>/ {{ totalPages }}</span>
      <button class="page-go-btn" @click="handleGoToPage">이동</button>
    </div>
  </nav>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  page: {
    type: Number,
    required: true,
  },
  size: {
    type: Number,
    required: true,
  },
  totalElements: {
    type: Number,
    required: true,
  },
  pageSizeOptions: {
    type: Array,
    default: () => [10, 20, 50],
  },
})

const emit = defineEmits(['update:page', 'update:size', 'change'])

const localGoToPage = ref(props.page)

// totalPages 계산
const totalPages = computed(() => {
  return Math.ceil(props.totalElements / props.size) || 1
})

// visiblePages 계산 (현재 페이지 ±2 범위)
const visiblePages = computed(() => {
  const pages = []
  const start = Math.max(1, props.page - 2)
  const end = Math.min(totalPages.value, props.page + 2)

  for (let i = start; i <= end; i++) {
    pages.push(i)
  }

  return pages
})

// page가 변경되면 localGoToPage도 동기화
watch(
  () => props.page,
  (newPage) => {
    localGoToPage.value = newPage
  }
)

const handlePageChange = (newPage) => {
  if (newPage >= 1 && newPage <= totalPages.value && newPage !== props.page) {
    emit('update:page', newPage)
    emit('change')
  }
}

const handleSizeChange = (newSize) => {
  if (newSize !== props.size) {
    emit('update:size', newSize)
    emit('update:page', 1)
    emit('change')
  }
}

const handleGoToPage = () => {
  if (localGoToPage.value >= 1 && localGoToPage.value <= totalPages.value) {
    handlePageChange(localGoToPage.value)
  }
}
</script>

<style scoped>
.base-pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid var(--dsx-color-neutral-border-alternative);
}

.page-size-select {
  padding: 8px 12px;
  border: 1px solid var(--dsx-color-neutral-border-default);
  border-radius: 6px;
  font-size: 13px;
  color: var(--dsx-color-neutral-text-default);
  background: var(--dsx-color-neutral-surface-default);
  cursor: pointer;
  transition: border-color 0.2s;
}

.page-size-select:focus {
  outline: none;
  border-color: var(--dsx-color-primary-fill-default);
}

.page-numbers {
  display: flex;
  gap: 4px;
}

.page-btn {
  min-width: 32px;
  height: 32px;
  padding: 0 8px;
  border: 1px solid var(--dsx-color-neutral-border-default);
  border-radius: 6px;
  background: var(--dsx-color-neutral-surface-default);
  color: var(--dsx-color-neutral-text-default);
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.page-btn:hover:not(:disabled) {
  background: var(--dsx-color-interaction-surface-disabled);
  border-color: var(--dsx-color-primary-fill-default);
  color: var(--dsx-color-primary-fill-default);
}

.page-btn.active {
  background: var(--dsx-color-primary-fill-default);
  color: var(--dsx-color-static-white);
  border-color: var(--dsx-color-primary-fill-default);
  font-weight: 600;
}

.page-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.page-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.page-input {
  width: 50px;
  padding: 6px 8px;
  border: 1px solid var(--dsx-color-neutral-border-default);
  border-radius: 6px;
  font-size: 13px;
  text-align: center;
}

.page-input:focus {
  outline: none;
  border-color: var(--dsx-color-primary-fill-default);
}

.page-info span {
  font-size: 13px;
  color: var(--dsx-color-neutral-text-alternative);
}

.page-go-btn {
  padding: 6px 12px;
  border: 1px solid var(--dsx-color-neutral-border-default);
  border-radius: 6px;
  background: var(--dsx-color-neutral-surface-default);
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.page-go-btn:hover {
  background: var(--dsx-color-interaction-surface-disabled);
  border-color: var(--dsx-color-primary-fill-default);
  color: var(--dsx-color-primary-fill-default);
}

@media (max-width: 768px) {
  .base-pagination {
    flex-direction: column;
    gap: 12px;
  }

  .page-numbers {
    order: -1;
  }
}
</style>
