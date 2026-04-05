export const batchHstRoute = {
  path: '/batch/history/batchHstMgt',
  name: 'BatchHst',
  component: () => import('./BatchHstPage.vue'),
  meta: { requiresAuth: true },
}
