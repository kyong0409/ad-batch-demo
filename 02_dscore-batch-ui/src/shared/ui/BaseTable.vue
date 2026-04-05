<template>
  <div class="table-wrapper">
    <table class="data-table">
      <caption v-if="caption" class="sr-only">{{ caption }}</caption>
      <thead>
        <tr>
          <th
            v-for="col in columns"
            :key="col.field"
            :style="col.width ? { width: col.width } : {}"
            :class="col.headerClass"
            scope="col"
          >
            {{ col.label }}
          </th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="row in data"
          :key="row[rowKey]"
          class="table-row"
          :class="{ 'table-row-clickable': clickable, 'table-row-selected': isSelected(row) }"
          :tabindex="clickable ? 0 : undefined"
          :aria-selected="isSelected(row) ? 'true' : undefined"
          @click="handleRowClick(row)"
          @keydown.enter="handleRowClick(row)"
        >
          <td
            v-for="col in columns"
            :key="col.field"
            :class="col.cellClass"
          >
            <!-- 커스텀 슬롯이 있으면 사용 -->
            <slot :name="col.field" :row="row" :value="row[col.field]">
              <!-- 포맷터가 있으면 적용 -->
              <template v-if="col.formatter">
                {{ col.formatter(row[col.field], row) }}
              </template>
              <template v-else>
                {{ row[col.field] }}
              </template>
            </slot>
          </td>
        </tr>
        <!-- 데이터 없음 -->
        <tr v-if="data.length === 0">
          <td :colspan="columns.length" class="table-empty" aria-live="polite">
            {{ loading ? loadingText : emptyText }}
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
const props = defineProps({
  columns: {
    type: Array,
    required: true,
    // [{ field: 'id', label: '아이디', width: '100px', headerClass: '', cellClass: '', formatter: (val, row) => val }]
  },
  data: {
    type: Array,
    default: () => [],
  },
  rowKey: {
    type: String,
    default: 'id',
  },
  loading: {
    type: Boolean,
    default: false,
  },
  emptyText: {
    type: String,
    default: '데이터가 없습니다.',
  },
  loadingText: {
    type: String,
    default: '불러오는 중...',
  },
  caption: {
    type: String,
    default: '',
  },
  clickable: {
    type: Boolean,
    default: true,
  },
  selectedRow: {
    type: Object,
    default: null,
  },
})

const emit = defineEmits(['rowClick'])

const handleRowClick = (row) => {
  if (props.clickable) {
    emit('rowClick', row)
  }
}

const isSelected = (row) => {
  if (!props.selectedRow) return false
  return row[props.rowKey] === props.selectedRow[props.rowKey]
}
</script>

<style scoped>
.table-wrapper {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th {
  padding: 12px 16px;
  text-align: left;
  font-size: 13px;
  font-weight: 600;
  color: var(--dsx-color-interaction-text-inactive);
  border-bottom: 2px solid var(--dsx-color-neutral-border-alternative);
  background: var(--dsx-color-neutral-surface-alternative);
  white-space: nowrap;
}

.data-table td {
  padding: 12px 16px;
  font-size: 13px;
  color: var(--dsx-color-neutral-text-default);
  border-bottom: 1px solid var(--dsx-color-interaction-surface-disabled);
}

.table-row {
  transition: background 0.15s;
}

.table-row-clickable {
  cursor: pointer;
}

.table-row-clickable:hover {
  background: var(--dsx-color-primary-fill-neutral);
}

.table-row-selected {
  background: var(--dsx-color-primary-surface-default);
}

.table-empty {
  text-align: center;
  color: var(--dsx-color-interaction-text-inactive);
  padding: 32px 16px !important;
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}
</style>
