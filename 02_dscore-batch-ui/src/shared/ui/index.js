/**
 * 공통 UI 컴포넌트
 *
 * 2개 이상의 페이지에서 사용하는 UI 컴포넌트를 관리합니다.
 *
 * 생성: npx @dscore/cli gc <component-name>
 *
 * 페이지 전용 컴포넌트는 pages/[feature]/components/ui/ 에 위치합니다.
 */

export { default as BasePagination } from './BasePagination.vue'
export { default as BaseDialog } from './BaseDialog.vue'
export { default as BaseTable } from './BaseTable.vue'
export { default as BaseSearchForm } from './BaseSearchForm.vue'
