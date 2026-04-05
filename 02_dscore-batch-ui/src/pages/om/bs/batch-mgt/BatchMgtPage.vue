<template>
  <div class="page-content">
    <section class="page-header">
      <h1 class="page-title">배치 관리</h1>
    </section>

    <!-- 검색 영역 -->
    <BaseSearchForm @search="fetchBatchList" @reset="resetSearch">
      <div class="form-group">
        <label class="form-label" for="batch-group">배치 그룹</label>
        <select id="batch-group" v-model="searchState.jobGroup" class="form-select">
          <option v-for="opt in options.BATCH_GROUP_NAME" :key="opt.value" :value="opt.value">{{ opt.label }}</option>
        </select>
      </div>
      <div class="form-group">
        <label class="form-label" for="batch-useyn">사용여부</label>
        <select id="batch-useyn" v-model="searchState.useYn" class="form-select">
          <option v-for="opt in options.USE_YN" :key="opt.value" :value="opt.value">{{ opt.label }}</option>
        </select>
      </div>
    </BaseSearchForm>

    <!-- 배치 리스트 -->
    <section class="list-section">
      <div class="list-header">
        <h2 class="section-title">
          배치 리스트
          <span class="total-count">(총 {{ pageParams.totalElements }}건)</span>
        </h2>
        <div class="header-actions">
          <button class="btn btn-success" @click="downloadExcel">엑셀 다운로드</button>
          <button class="btn btn-primary" @click="openRegisterDialog">배치 등록</button>
        </div>
      </div>

      <BaseTable
        :columns="BATCH_MGT_CONSTANTS.TABLE_HEADERS"
        :data="batchData"
        row-key="jobNm"
        :loading="loading"
        empty-text="배치 정보가 없습니다."
        @row-click="onRowClick"
      >
        <template #jobNm="{ row }">
          <span class="link-cell">{{ row.jobNm }}</span>
        </template>
        <template #paramSbst="{ row }">
          <span class="param-cell">{{ row.paramSbst }}</span>
        </template>
      </BaseTable>

      <!-- 페이지네이션 -->
      <BasePagination
        v-model:page="currentPage"
        v-model:size="pageSize"
        :total-elements="pageParams.totalElements"
        @change="fetchBatchList"
      />
    </section>

    <!-- 배치 상세/등록 다이얼로그 -->
    <BaseDialog
      v-model="batchDetailOpen"
      :title="isEditMode ? '배치 상세' : '배치 등록'"
      size="lg"
    >
      <div class="form-row">
        <div class="form-field">
          <label class="form-field-label"><span class="required">*</span> 배치 그룹</label>
          <select v-model="selectedBatch.jobGroup" class="form-select" :disabled="isEditMode">
            <option v-for="opt in options.BATCH_GROUP_NAME" :key="opt.value" :value="opt.value">{{ opt.label }}</option>
          </select>
        </div>
        <div class="form-field">
          <label class="form-field-label"><span class="required">*</span> Job Name</label>
          <input
            v-model="selectedBatch.jobNm"
            type="text"
            class="form-input"
            :disabled="isEditMode"
            placeholder="Job Name 입력"
          />
        </div>
      </div>

      <div class="form-row">
        <div class="form-field full-width">
          <label class="form-field-label">파라미터(JSON)</label>
          <textarea
            v-model="selectedBatch.paramSbst"
            class="form-textarea"
            placeholder="파라미터(JSON) 입력"
            rows="3"
          ></textarea>
        </div>
      </div>

      <div class="form-row">
        <div class="form-field full-width">
          <label class="form-field-label"><span class="required">*</span> 실행주기</label>
          <input
            v-model="selectedBatch.perdSbst"
            type="text"
            class="form-input"
            placeholder="실행주기(크론식) 입력 - ex) 0 0 10 * * ?"
          />
        </div>
      </div>

      <div class="form-row">
        <div class="form-field">
          <label class="form-field-label"><span class="required">*</span> 사용여부</label>
          <div class="radio-group">
            <label class="radio-label">
              <input type="radio" v-model="selectedBatch.useYn" value="Y" />
              사용
            </label>
            <label class="radio-label">
              <input type="radio" v-model="selectedBatch.useYn" value="N" />
              미사용
            </label>
          </div>
        </div>
      </div>

      <div class="form-row">
        <div class="form-field full-width">
          <label class="form-field-label">설명</label>
          <textarea
            v-model="selectedBatch.jobDesc"
            class="form-textarea"
            placeholder="배치 스케줄에 대한 설명(부가정보) 입력"
            rows="3"
          ></textarea>
        </div>
      </div>

      <template #actions>
        <button class="btn btn-default" @click="closeDetail">취소</button>
        <button class="btn btn-primary" @click="saveBatch">
          {{ isEditMode ? '저장' : '등록' }}
        </button>
      </template>
    </BaseDialog>
  </div>
</template>

<script setup>
import { BasePagination, BaseDialog, BaseTable, BaseSearchForm } from '@/shared'
import { useBatchMgtPage } from './composables/useBatchMgtPage'
import { BATCH_MGT_CONSTANTS } from './constants'

const {
  loading,
  searchState,
  pageParams,
  batchData,
  selectedBatch,
  batchDetailOpen,
  isEditMode,
  options,
  fetchBatchList,
  onRowClick,
  closeDetail,
  openRegisterDialog,
  saveBatch,
  resetSearch,
  downloadExcel,
  currentPage,
  pageSize,
} = useBatchMgtPage()
</script>

<style scoped>
/* 페이지 전용 스타일 */
.param-cell {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: inline-block;
}
</style>
