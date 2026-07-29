<script setup>
import AqiBadge from './AqiBadge.vue'

defineProps({
  categories: {
    type: Array,
    default: () => [],
  },
})
</script>

<template>
  <ul class="flex flex-col gap-1.5">
    <li
      v-for="category in categories"
      :key="category.id ?? category.contaminent"
      class="flex items-center gap-2.5 text-sm"
    >
      <span class="w-12 font-semibold text-slate-900 dark:text-slate-100">{{ category.contaminent }}</span>
      <span class="w-16 text-slate-500 dark:text-slate-400">{{ category.contaminentValue }}</span>
      <AqiBadge :category="category.aqiCategory" />
    </li>
  </ul>
</template>
