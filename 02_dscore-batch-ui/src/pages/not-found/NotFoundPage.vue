<template>
  <div class="not-found-page">
    <div class="not-found-content">
      <h1 class="error-code">404</h1>
      <p class="error-message">페이지를 찾을 수 없습니다</p>
      <p class="error-description">요청하신 페이지가 존재하지 않거나 이동되었을 수 있습니다.</p>
      <div class="action-buttons">
        <button class="btn btn-primary" @click="goHome">홈으로 돌아가기</button>
        <button class="btn btn-secondary" @click="goBack">이전 페이지로</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push({ name: 'Home' })
}

const goBack = () => {
  router.back()
}
</script>

<style scoped>
.not-found-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: var(--color-bg-page);
}

.not-found-content {
  text-align: center;
  padding: 40px;
}

.error-code {
  font-size: 120px;
  font-weight: 800;
  color: var(--color-primary);
  line-height: 1;
  margin: 0 0 16px;
}

.error-message {
  font-size: 24px;
  font-weight: 600;
  color: var(--color-text-secondary);
  margin: 0 0 8px;
}

.error-description {
  font-size: 15px;
  color: var(--color-text-light);
  margin: 0 0 32px;
}

.action-buttons {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.btn {
  padding: 10px 24px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-primary {
  background: var(--color-primary);
  color: var(--dsx-color-static-white);
}

.btn-primary:hover {
  background: var(--dsx-color-primary-fill-default);
}

.btn-secondary {
  background: var(--dsx-color-interaction-surface-disabled);
  color: var(--dsx-color-neutral-text-neutral);
}

.btn-secondary:hover {
  background: var(--dsx-color-neutral-border-default);
}
</style>
