<script setup>
import { ref } from 'vue'
import { useAirPollutionStore } from '../stores/airPollution'
import { SUPPORTED_CITIES } from '../constants/cities'
import CitySelect from '../components/CitySelect.vue'
import CategoryList from '../components/CategoryList.vue'

const store = useAirPollutionStore()

const city = ref(SUPPORTED_CITIES[0])
const startDate = ref('')
const endDate = ref('')

function submit() {
  store.fetchHistory(city.value, startDate.value || undefined, endDate.value || undefined)
}

async function remove(date) {
  if (!confirm(`Delete the record for ${store.history.city} on ${date}?`)) {
    return
  }
  try {
    await store.deleteByCityAndDate(store.history.city, date)
  } catch {
    alert('Could not delete this record.')
  }
}
</script>

<template>
  <section class="flex flex-col gap-4">
    <div>
      <h1 class="text-2xl font-semibold text-slate-900 dark:text-slate-100">Fetch Air Pollution History</h1>
      <p class="mt-1 text-sm text-slate-500 dark:text-slate-400">
        Fetches air pollution history for a city and date range. Missing dates are pulled from
        OpenWeatherMap and saved automatically; if left empty, the last week is used.
      </p>
    </div>

    <form
      class="flex flex-wrap items-end gap-4 rounded-xl border border-slate-200 bg-white p-4 shadow-sm dark:border-slate-800 dark:bg-slate-900"
      @submit.prevent="submit"
    >
      <label class="flex flex-col gap-1.5 text-sm text-slate-600 dark:text-slate-400">
        <span>City</span>
        <CitySelect v-model="city" />
      </label>
      <label class="flex flex-col gap-1.5 text-sm text-slate-600 dark:text-slate-400">
        <span>Start date</span>
        <input
          v-model="startDate"
          type="date"
          class="rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900 shadow-sm outline-none transition-colors focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 dark:border-slate-700 dark:bg-slate-900 dark:text-slate-100 dark:focus:border-blue-400 dark:[color-scheme:dark]"
        />
      </label>
      <label class="flex flex-col gap-1.5 text-sm text-slate-600 dark:text-slate-400">
        <span>End date</span>
        <input
          v-model="endDate"
          type="date"
          class="rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900 shadow-sm outline-none transition-colors focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 dark:border-slate-700 dark:bg-slate-900 dark:text-slate-100 dark:focus:border-blue-400 dark:[color-scheme:dark]"
        />
      </label>
      <button
        type="submit"
        :disabled="store.historyLoading"
        class="rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white shadow-sm transition-colors hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-50 dark:bg-blue-500 dark:hover:bg-blue-600"
      >
        {{ store.historyLoading ? 'Fetching...' : 'Fetch History' }}
      </button>
    </form>

    <p v-if="store.historyError" class="text-sm text-red-600 dark:text-red-400">{{ store.historyError }}</p>

    <div v-if="store.history">
      <h2 class="mb-3 text-lg font-semibold text-slate-900 dark:text-slate-100">{{ store.history.city }}</h2>
      <p v-if="store.history.results.length === 0" class="py-4 text-sm text-slate-500 dark:text-slate-400">
        No data for this range.
      </p>
      <div v-else class="overflow-x-auto rounded-xl border border-slate-200 shadow-sm dark:border-slate-800">
        <table class="w-full border-collapse text-sm">
          <thead>
            <tr class="bg-slate-50 dark:bg-slate-900">
              <th class="px-4 py-2.5 text-left text-xs font-semibold uppercase tracking-wide text-slate-500 dark:text-slate-400">
                Date
              </th>
              <th class="px-4 py-2.5 text-left text-xs font-semibold uppercase tracking-wide text-slate-500 dark:text-slate-400">
                Categories
              </th>
              <th class="px-4 py-2.5"></th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-200 bg-white dark:divide-slate-800 dark:bg-slate-900/40">
            <tr v-for="result in store.history.results" :key="result.date">
              <td class="px-4 py-3 align-top text-slate-700 dark:text-slate-300">{{ result.date }}</td>
              <td class="px-4 py-3 align-top"><CategoryList :categories="result.categories" /></td>
              <td class="px-4 py-3 align-top">
                <button
                  class="rounded-lg border border-red-300 px-3 py-1.5 text-xs font-medium text-red-600 transition-colors hover:bg-red-600 hover:text-white dark:border-red-800 dark:text-red-400 dark:hover:bg-red-600 dark:hover:text-white"
                  @click="remove(result.date)"
                >
                  Delete
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </section>
</template>
