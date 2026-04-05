export const notFoundRoute = {
  path: '/:pathMatch(.*)*',
  name: 'NotFound',
  component: () => import('./NotFoundPage.vue'),
  meta: { layout: 'blank' },
}
