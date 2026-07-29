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
  <ul class="category-list">
    <li v-for="category in categories" :key="category.id ?? category.contaminent" class="category-list__item">
      <span class="category-list__name">{{ category.contaminent }}</span>
      <span class="category-list__value">{{ category.contaminentValue }}</span>
      <AqiBadge :category="category.aqiCategory" />
    </li>
  </ul>
</template>

<style scoped>
.category-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.category-list__item {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  font-size: 0.9rem;
}

.category-list__name {
  font-weight: 600;
  width: 3rem;
}

.category-list__value {
  color: var(--text);
  width: 4rem;
}
</style>
