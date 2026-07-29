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
    class="city-select"
    :value="modelValue"
    @change="$emit('update:modelValue', $event.target.value)"
  >
    <option v-if="includeAll" value="">All cities</option>
    <option v-for="city in SUPPORTED_CITIES" :key="city" :value="city">{{ city }}</option>
  </select>
</template>

<style scoped>
.city-select {
  padding: 0.5rem 0.75rem;
  border: 1px solid var(--border);
  border-radius: 6px;
  background: var(--bg);
  color: var(--text-h);
  font: inherit;
}
</style>
