<script setup>
import { ref, onMounted } from 'vue'
import { useAirPollutionStore } from '../stores/airPollution'
import CitySelect from '../components/CitySelect.vue'
import CategoryList from '../components/CategoryList.vue'

const store = useAirPollutionStore()
const cityFilter = ref('')

function load() {
  if (cityFilter.value) {
    store.loadByCity(cityFilter.value)
  } else {
    store.loadAll({ page: 0 })
  }
}

function goToPage(page) {
  store.loadAll({ page })
}

async function remove(record) {
  if (!confirm(`Delete the record for ${record.city} on ${record.date}?`)) {
    return
  }
  try {
    await store.deleteById(record.id)
  } catch {
    alert('Could not delete this record.')
  }
}

onMounted(load)
</script>

<template>
  <section class="view">
    <h1>Stored Records</h1>

    <div class="filters">
      <CitySelect v-model="cityFilter" include-all @update:modelValue="load" />
    </div>

    <p v-if="store.loading" class="state">Loading...</p>
    <p v-else-if="store.error" class="state state--error">{{ store.error }}</p>
    <p v-else-if="store.records.length === 0" class="state">No records found.</p>

    <table v-else class="table">
      <thead>
        <tr>
          <th>City</th>
          <th>Date</th>
          <th>Categories</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="record in store.records" :key="record.id">
          <td>{{ record.city }}</td>
          <td>{{ record.date }}</td>
          <td><CategoryList :categories="record.categories" /></td>
          <td>
            <button class="btn btn--danger" @click="remove(record)">Delete</button>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="!cityFilter" class="pagination">
      <button class="btn" :disabled="store.page === 0" @click="goToPage(store.page - 1)">Previous</button>
      <span>Page {{ store.page + 1 }}</span>
      <button class="btn" :disabled="store.records.length < store.size" @click="goToPage(store.page + 1)">
        Next
      </button>
    </div>
  </section>
</template>
