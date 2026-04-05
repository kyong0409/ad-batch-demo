<template>
  <Teleport to="body">
    <Transition name="dialog-fade">
      <div
        v-if="modelValue"
        class="dialog-overlay"
        @click.self="handleOverlayClick"
        @keydown.esc="close"
        ref="overlayRef"
      >
        <div
          class="dialog-card"
          :class="sizeClass"
          role="dialog"
          aria-modal="true"
          :aria-labelledby="title ? 'dialog-title-' + uid : undefined"
          ref="dialogRef"
          tabindex="-1"
        >
          <!-- 헤더 -->
          <div v-if="title || $slots.header" class="dialog-header">
            <slot name="header">
              <h2 class="dialog-title" :id="'dialog-title-' + uid">{{ title }}</h2>
            </slot>
            <button
              v-if="showCloseButton"
              class="dialog-close-btn"
              @click="close"
              aria-label="닫기"
            >
              &times;
            </button>
          </div>

          <!-- 본문 -->
          <div class="dialog-body">
            <slot />
          </div>

          <!-- 푸터 (액션 버튼) -->
          <div v-if="$slots.actions" class="dialog-actions">
            <slot name="actions" />
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { computed, ref, watch, nextTick, onBeforeUnmount, getCurrentInstance } from 'vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  title: {
    type: String,
    default: '',
  },
  size: {
    type: String,
    default: 'medium',
    validator: (value) => ['small', 'medium', 'large', 'wide'].includes(value),
  },
  closeOnOverlay: {
    type: Boolean,
    default: true,
  },
  showCloseButton: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['update:modelValue', 'close'])

const uid = getCurrentInstance()?.uid
const overlayRef = ref(null)
const dialogRef = ref(null)
let previouslyFocused = null

const sizeClass = computed(() => `dialog-card--${props.size}`)

const close = () => {
  emit('update:modelValue', false)
  emit('close')
}

const handleOverlayClick = () => {
  if (props.closeOnOverlay) {
    close()
  }
}

const trapFocus = (e) => {
  if (e.key !== 'Tab' || !dialogRef.value) return
  const focusable = dialogRef.value.querySelectorAll(
    'a[href], button:not([disabled]), input:not([disabled]), select:not([disabled]), textarea:not([disabled]), [tabindex]:not([tabindex="-1"])'
  )
  if (focusable.length === 0) return
  const first = focusable[0]
  const last = focusable[focusable.length - 1]
  if (e.shiftKey) {
    if (document.activeElement === first) { e.preventDefault(); last.focus() }
  } else {
    if (document.activeElement === last) { e.preventDefault(); first.focus() }
  }
}

watch(() => props.modelValue, async (val) => {
  if (val) {
    previouslyFocused = document.activeElement
    await nextTick()
    dialogRef.value?.focus()
    document.addEventListener('keydown', trapFocus)
  } else {
    document.removeEventListener('keydown', trapFocus)
    await nextTick()
    previouslyFocused?.focus()
    previouslyFocused = null
  }
})

onBeforeUnmount(() => {
  document.removeEventListener('keydown', trapFocus)
})
</script>

<style scoped>
.dialog-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 16px;
}

.dialog-card {
  width: 100%;
  padding: 28px 32px;
  background: var(--dsx-color-neutral-surface-default);
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-height: 85vh;
  overflow-y: auto;
}

/* 사이즈 변형 */
.dialog-card--small {
  max-width: 400px;
}

.dialog-card--medium {
  max-width: 560px;
}

.dialog-card--large {
  max-width: 720px;
}

.dialog-card--wide {
  max-width: 900px;
}

.dialog-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.dialog-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--dsx-color-neutral-text-default);
  margin: 0;
}

.dialog-close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  font-size: 24px;
  color: var(--dsx-color-interaction-text-inactive);
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s;
}

.dialog-close-btn:hover {
  background: var(--dsx-color-interaction-surface-disabled);
  color: var(--dsx-color-neutral-text-default);
}

.dialog-body {
  flex: 1;
  overflow-y: auto;
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid var(--dsx-color-neutral-border-alternative);
}

/* 트랜지션 */
.dialog-fade-enter-active,
.dialog-fade-leave-active {
  transition: opacity 0.2s ease;
}

.dialog-fade-enter-active .dialog-card,
.dialog-fade-leave-active .dialog-card {
  transition: transform 0.2s ease;
}

.dialog-fade-enter-from,
.dialog-fade-leave-to {
  opacity: 0;
}

.dialog-fade-enter-from .dialog-card,
.dialog-fade-leave-to .dialog-card {
  transform: scale(0.95);
}

@media (max-width: 768px) {
  .dialog-card {
    max-width: 100% !important;
    margin: 0;
    padding: 20px;
  }
}
</style>
