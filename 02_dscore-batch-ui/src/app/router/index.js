import { createRouter, createWebHistory } from 'vue-router'
import { batchMgtRoute } from '@/pages/om/bs/batch-mgt/route'
import { batchHstRoute } from '@/pages/om/bs/batch-hst/route'
import { notFoundRoute } from '@/pages/not-found/route'

const PREFIX = '/om/bs'

const batchRoutes = [batchMgtRoute, batchHstRoute].map((route) => ({
  ...route,
  path: PREFIX + route.path,
  meta: { ...route.meta, layout: 'default', requiresAuth: false },
}))

const routes = [
  {
    path: '/',
    redirect: '/om/bs/batch/manage/batchMgt',
  },
  ...batchRoutes,
  notFoundRoute,
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
