<script setup>
import { ref } from 'vue'
import { useGeocodeStore } from '../stores/geocode'

const store = useGeocodeStore()
const city = ref('')

function submit() {
  if (city.value.trim()) {
    store.lookup(city.value.trim())
  }
}
</script>

<template>
  <section class="view">
    <h1>Geocode Lookup</h1>
    <p class="hint">Look up the coordinates of any city name.</p>

    <form class="form" @submit.prevent="submit">
      <label class="field">
        <span>City name</span>
        <input v-model="city" type="text" placeholder="e.g. London" />
      </label>
      <button class="btn btn--primary" type="submit" :disabled="store.loading">
        {{ store.loading ? 'Searching...' : 'Search' }}
      </button>
    </form>

    <p v-if="store.error" class="state state--error">{{ store.error }}</p>

    <div v-if="store.result" class="card">
      <p><strong>City:</strong> {{ store.result.city }}</p>
      <p><strong>Latitude:</strong> {{ store.result.lat }}</p>
      <p><strong>Longitude:</strong> {{ store.result.lon }}</p>
    </div>
  </section>
</template>
