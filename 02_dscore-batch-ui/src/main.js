import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './app/App.vue'
import router from './app/router'
import { setupMock } from './app/plugins/mockSetup'
import '@/assets/styles/index.css'
import { config } from '@/shared/config'

const bootstrap = async () => {
  await setupMock()

  if (import.meta.env.DEV) console.debug('[app:config]', config)

  const app = createApp(App)
  app.use(createPinia())
  app.use(router)

  app.mount('#app')
}

bootstrap()
