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
  <section class="flex flex-col gap-4">
    <h1 class="text-2xl font-semibold text-slate-900 dark:text-slate-100">Stored Records</h1>

    <div class="flex items-center gap-3">
      <CitySelect v-model="cityFilter" include-all @update:modelValue="load" />
    </div>

    <p v-if="store.loading" class="py-4 text-sm text-slate-500 dark:text-slate-400">Loading...</p>
    <p v-else-if="store.error" class="py-4 text-sm text-red-600 dark:text-red-400">{{ store.error }}</p>
    <p v-else-if="store.records.length === 0" class="py-4 text-sm text-slate-500 dark:text-slate-400">
      No records found.
    </p>

    <div v-else class="overflow-x-auto rounded-xl border border-slate-200 shadow-sm dark:border-slate-800">
      <table class="w-full border-collapse text-sm">
        <thead>
          <tr class="bg-slate-50 dark:bg-slate-900">
            <th class="px-4 py-2.5 text-left text-xs font-semibold uppercase tracking-wide text-slate-500 dark:text-slate-400">
              City
            </th>
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
          <tr v-for="record in store.records" :key="record.id">
            <td class="px-4 py-3 align-top font-medium text-slate-900 dark:text-slate-100">{{ record.city }}</td>
            <td class="px-4 py-3 align-top text-slate-700 dark:text-slate-300">{{ record.date }}</td>
            <td class="px-4 py-3 align-top"><CategoryList :categories="record.categories" /></td>
            <td class="px-4 py-3 align-top">
              <button
                class="rounded-lg border border-red-300 px-3 py-1.5 text-xs font-medium text-red-600 transition-colors hover:bg-red-600 hover:text-white dark:border-red-800 dark:text-red-400 dark:hover:bg-red-600 dark:hover:text-white"
                @click="remove(record)"
              >
                Delete
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="!cityFilter" class="flex items-center gap-4">
      <button
        class="rounded-lg border border-slate-300 px-3 py-1.5 text-sm text-slate-700 transition-colors hover:border-blue-500 hover:text-blue-600 disabled:cursor-not-allowed disabled:opacity-50 dark:border-slate-700 dark:text-slate-300 dark:hover:border-blue-400 dark:hover:text-blue-400"
        :disabled="store.page === 0"
        @click="goToPage(store.page - 1)"
      >
        Previous
      </button>
      <span class="text-sm text-slate-500 dark:text-slate-400">Page {{ store.page + 1 }}</span>
      <button
        class="rounded-lg border border-slate-300 px-3 py-1.5 text-sm text-slate-700 transition-colors hover:border-blue-500 hover:text-blue-600 disabled:cursor-not-allowed disabled:opacity-50 dark:border-slate-700 dark:text-slate-300 dark:hover:border-blue-400 dark:hover:text-blue-400"
        :disabled="store.records.length < store.size"
        @click="goToPage(store.page + 1)"
      >
        Next
      </button>
    </div>
  </section>
</template>
