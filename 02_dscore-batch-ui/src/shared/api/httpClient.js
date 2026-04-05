/**
 * Axios 인스턴스 (Singleton)
 *
 * 모든 API 호출은 이 인스턴스를 통해 수행합니다.
 * axios를 직접 import하지 마세요.
 */
import axios from 'axios'
import { config } from '@/shared/config'

const api = axios.create({
  baseURL: config.apiBaseUrl,
  timeout: config.apiTimeout,
  withCredentials: true,
  headers: { 'Content-Type': 'application/json' },
})

export default api
