<script setup>
import { computed } from 'vue'
import { AQI_CATEGORY_LABELS } from '../constants/aqi'

const props = defineProps({
  category: {
    type: String,
    default: null,
  },
})

const styles = {
  good: 'bg-emerald-100 text-emerald-700 dark:bg-emerald-900/40 dark:text-emerald-300',
  satisfactory: 'bg-lime-100 text-lime-700 dark:bg-lime-900/40 dark:text-lime-300',
  moderate: 'bg-amber-100 text-amber-700 dark:bg-amber-900/40 dark:text-amber-300',
  poor: 'bg-orange-100 text-orange-700 dark:bg-orange-900/40 dark:text-orange-300',
  severe: 'bg-red-100 text-red-700 dark:bg-red-900/40 dark:text-red-300',
  hazardous: 'bg-purple-100 text-purple-700 dark:bg-purple-900/40 dark:text-purple-300',
}

const badgeClass = computed(
  () => styles[props.category?.toLowerCase()] ?? 'bg-slate-200 text-slate-600 dark:bg-slate-800 dark:text-slate-400',
)
</script>

<template>
  <span
    class="inline-block whitespace-nowrap rounded-full px-2.5 py-0.5 text-xs font-semibold"
    :class="badgeClass"
  >
    {{ category ? AQI_CATEGORY_LABELS[category] || category : 'Unknown' }}
  </span>
</template>
