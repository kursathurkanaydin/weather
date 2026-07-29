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
  <section class="flex flex-col gap-4">
    <div>
      <h1 class="text-2xl font-semibold text-slate-900 dark:text-slate-100">Geocode Lookup</h1>
      <p class="mt-1 text-sm text-slate-500 dark:text-slate-400">Look up the coordinates of any city name.</p>
    </div>

    <form
      class="flex flex-wrap items-end gap-4 rounded-xl border border-slate-200 bg-white p-4 shadow-sm dark:border-slate-800 dark:bg-slate-900"
      @submit.prevent="submit"
    >
      <label class="flex flex-col gap-1.5 text-sm text-slate-600 dark:text-slate-400">
        <span>City name</span>
        <input
          v-model="city"
          type="text"
          placeholder="e.g. London"
          class="rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900 shadow-sm outline-none transition-colors focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 dark:border-slate-700 dark:bg-slate-900 dark:text-slate-100 dark:focus:border-blue-400"
        />
      </label>
      <button
        type="submit"
        :disabled="store.loading"
        class="rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white shadow-sm transition-colors hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-50 dark:bg-blue-500 dark:hover:bg-blue-600"
      >
        {{ store.loading ? 'Searching...' : 'Search' }}
      </button>
    </form>

    <p v-if="store.error" class="text-sm text-red-600 dark:text-red-400">{{ store.error }}</p>

    <div
      v-if="store.result"
      class="flex flex-col gap-1.5 rounded-xl border border-slate-200 bg-white p-4 shadow-sm dark:border-slate-800 dark:bg-slate-900"
    >
      <p class="text-sm text-slate-700 dark:text-slate-300">
        <strong class="text-slate-900 dark:text-slate-100">City:</strong> {{ store.result.city }}
      </p>
      <p class="text-sm text-slate-700 dark:text-slate-300">
        <strong class="text-slate-900 dark:text-slate-100">Latitude:</strong> {{ store.result.lat }}
      </p>
      <p class="text-sm text-slate-700 dark:text-slate-300">
        <strong class="text-slate-900 dark:text-slate-100">Longitude:</strong> {{ store.result.lon }}
      </p>
    </div>
  </section>
</template>
