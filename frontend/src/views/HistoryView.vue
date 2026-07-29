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
  <section class="view">
    <h1>Fetch Air Pollution History</h1>
    <p class="hint">
      Fetches air pollution history for a city and date range. Missing dates are pulled from
      OpenWeatherMap and saved automatically; if left empty, the last week is used.
    </p>

    <form class="form" @submit.prevent="submit">
      <label class="field">
        <span>City</span>
        <CitySelect v-model="city" />
      </label>
      <label class="field">
        <span>Start date</span>
        <input v-model="startDate" type="date" />
      </label>
      <label class="field">
        <span>End date</span>
        <input v-model="endDate" type="date" />
      </label>
      <button class="btn btn--primary" type="submit" :disabled="store.historyLoading">
        {{ store.historyLoading ? 'Fetching...' : 'Fetch History' }}
      </button>
    </form>

    <p v-if="store.historyError" class="state state--error">{{ store.historyError }}</p>

    <div v-if="store.history">
      <h2>{{ store.history.city }}</h2>
      <p v-if="store.history.results.length === 0" class="state">No data for this range.</p>
      <table v-else class="table">
        <thead>
          <tr>
            <th>Date</th>
            <th>Categories</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="result in store.history.results" :key="result.date">
            <td>{{ result.date }}</td>
            <td><CategoryList :categories="result.categories" /></td>
            <td>
              <button class="btn btn--danger" @click="remove(result.date)">Delete</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>
