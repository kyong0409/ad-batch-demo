<template>
  <component :is="layout">
    <router-view />
  </component>
</template>

<script setup>
import { shallowRef, watch } from 'vue'
import { useRoute } from 'vue-router'
import AppLayout from './layouts/AppLayout.vue'
import BlankLayout from './layouts/BlankLayout.vue'

const route = useRoute()
const layout = shallowRef(BlankLayout)

watch(
  () => route.meta.layout,
  (layoutName) => {
    layout.value = layoutName === 'default' ? AppLayout : BlankLayout
  },
  { immediate: true },
)
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
</style>
