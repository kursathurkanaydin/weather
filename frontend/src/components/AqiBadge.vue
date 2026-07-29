<script setup>
import { AQI_CATEGORY_LABELS } from '../constants/aqi'

defineProps({
  category: {
    type: String,
    default: null,
  },
})
</script>

<template>
  <span v-if="category" class="aqi-badge" :class="`aqi-badge--${category.toLowerCase()}`">
    {{ AQI_CATEGORY_LABELS[category] || category }}
  </span>
  <span v-else class="aqi-badge aqi-badge--unknown">Unknown</span>
</template>

<style scoped>
.aqi-badge {
  display: inline-block;
  padding: 0.15rem 0.6rem;
  border-radius: 999px;
  font-size: 0.8rem;
  font-weight: 600;
  white-space: nowrap;
}

.aqi-badge--good {
  background: #d9f2df;
  color: #1c7a3d;
}

.aqi-badge--satisfactory {
  background: #e2f2d9;
  color: #4d7a1c;
}

.aqi-badge--moderate {
  background: #fdf1c8;
  color: #8a6a00;
}

.aqi-badge--poor {
  background: #fde1c8;
  color: #9a4d00;
}

.aqi-badge--severe {
  background: #fbd4d4;
  color: #a11c1c;
}

.aqi-badge--hazardous {
  background: #e6c8d8;
  color: #6b1240;
}

.aqi-badge--unknown {
  background: var(--border);
  color: var(--text);
}
</style>
