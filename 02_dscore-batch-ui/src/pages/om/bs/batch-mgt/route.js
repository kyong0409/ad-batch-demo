export const batchMgtRoute = {
  path: '/batch/manage/batchMgt',
  name: 'BatchMgt',
  component: () => import('./BatchMgtPage.vue'),
  meta: { requiresAuth: true },
}
