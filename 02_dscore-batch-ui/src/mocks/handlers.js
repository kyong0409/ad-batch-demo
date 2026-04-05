import { BATCH_HISTORY_HANDLER, BATCH_HISTORY_HANDLER_INFO } from './BATCH_HISTORY_HANDLER.js';
import { BATCH_SCHEDULE_HANDLER, BATCH_SCHEDULE_HANDLER_INFO } from './BATCH_SCHEDULE_HANDLER.js';
import { COMCD_MGT_HANDLER, COMCD_MGT_HANDLER_INFO } from './COMCD_MGT_HANDLER.js';

export const handlers = [
  ...BATCH_HISTORY_HANDLER,
  ...BATCH_SCHEDULE_HANDLER,
  ...COMCD_MGT_HANDLER,
];

export const handlerInfos = [
  BATCH_HISTORY_HANDLER_INFO,
  BATCH_SCHEDULE_HANDLER_INFO,
  COMCD_MGT_HANDLER_INFO,
];

export function logRegisteredHandlers() {
  const totalEndpoints = handlerInfos.reduce((sum, info) => sum + info.endpoints.length, 0);
  console.log(`%c[MSW] Mock handlers registered (${totalEndpoints} endpoints)`, 'color: #4CAF50; font-weight: bold');

  handlerInfos.forEach(info => {
    console.groupCollapsed(`%c[MSW] 📦 ${info.domain.toUpperCase()} (${info.endpoints.length})`, 'color: #2196F3');
    info.endpoints.forEach(ep => {
      console.log(`  ${ep.method.padEnd(6)} ${ep.path}`);
    });
    console.groupEnd();
  });
}
