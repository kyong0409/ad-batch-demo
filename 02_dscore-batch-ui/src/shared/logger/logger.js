const LEVELS = { debug: 0, info: 1, warn: 2, error: 3, silent: 4 }

const level = import.meta.env.VITE_LOG_LEVEL || 'debug'

export const logger = {
  debug: (...args) => LEVELS[level] <= LEVELS.debug && console.debug(...args),
  info: (...args) => LEVELS[level] <= LEVELS.info && console.info(...args),
  warn: (...args) => LEVELS[level] <= LEVELS.warn && console.warn(...args),
  error: (...args) => LEVELS[level] <= LEVELS.error && console.error(...args),
}