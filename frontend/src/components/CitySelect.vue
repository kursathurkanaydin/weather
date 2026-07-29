<script setup>
import { SUPPORTED_CITIES } from '../constants/cities'

defineProps({
  modelValue: {
    type: String,
    default: '',
  },
  includeAll: {
    type: Boolean,
    default: false,
  },
})

defineEmits(['update:modelValue'])
</script>

<template>
  <select
    class="rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900 shadow-sm outline-none transition-colors focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 dark:border-slate-700 dark:bg-slate-900 dark:text-slate-100 dark:focus:border-blue-400"
    :value="modelValue"
    @change="$emit('update:modelValue', $event.target.value)"
  >
    <option v-if="includeAll" value="">All cities</option>
    <option v-for="city in SUPPORTED_CITIES" :key="city" :value="city">{{ city }}</option>
  </select>
</template>
