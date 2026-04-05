<template>
  <footer class="app-footer">
    <span>{{ FOOTER_TEXT }}</span>
  </footer>
</template>

<script setup>
import { FOOTER_TEXT } from './constants'
</script>

<style scoped>
.app-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px 32px;
  background: var(--dsx-color-neutral-surface-default);
  border-top: 1px solid var(--dsx-color-neutral-border-default);
  font-size: 12px;
  color: var(--dsx-color-interaction-text-inactive);
}
</style>
