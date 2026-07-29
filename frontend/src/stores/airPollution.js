import { defineStore } from 'pinia'
import {
  getAllAirPollutions,
  getAirPollutionsByCity,
  getAirPollutionsByCityAndDateRange,
  fetchAirPollutionHistory,
  deleteAirPollutionById,
  deleteAirPollutionByCityAndDate,
} from '../api/airPollution'
import { extractErrorMessage } from '../api/errors'

export const useAirPollutionStore = defineStore('airPollution', {
  state: () => ({
    records: [],
    page: 0,
    size: 10,
    sortBy: 'date',
    ascending: true,
    loading: false,
    error: null,
    history: null,
    historyLoading: false,
    historyError: null,
  }),

  actions: {
    async loadAll({ page = this.page, size = this.size, sortBy = this.sortBy, ascending = this.ascending } = {}) {
      this.loading = true
      this.error = null
      try {
        this.records = await getAllAirPollutions({ page, size, sortBy, ascending })
        this.page = page
        this.size = size
        this.sortBy = sortBy
        this.ascending = ascending
      } catch (err) {
        this.error = extractErrorMessage(err)
        this.records = []
      } finally {
        this.loading = false
      }
    },

    async loadByCity(city) {
      this.loading = true
      this.error = null
      try {
        this.records = await getAirPollutionsByCity(city)
      } catch (err) {
        this.error = extractErrorMessage(err)
        this.records = []
      } finally {
        this.loading = false
      }
    },

    async loadByCityAndDateRange(city, startDate, endDate) {
      this.loading = true
      this.error = null
      try {
        this.records = await getAirPollutionsByCityAndDateRange(city, startDate, endDate)
      } catch (err) {
        this.error = extractErrorMessage(err)
        this.records = []
      } finally {
        this.loading = false
      }
    },

    async fetchHistory(city, startDate, endDate) {
      this.historyLoading = true
      this.historyError = null
      this.history = null
      try {
        this.history = await fetchAirPollutionHistory(city, startDate, endDate)
      } catch (err) {
        this.historyError = extractErrorMessage(err)
      } finally {
        this.historyLoading = false
      }
    },

    async deleteById(id) {
      await deleteAirPollutionById(id)
      this.records = this.records.filter((record) => record.id !== id)
    },

    async deleteByCityAndDate(city, date) {
      await deleteAirPollutionByCityAndDate(city, date)
      this.records = this.records.filter((record) => !(record.city === city && record.date === date))
      if (this.history) {
        this.history.results = this.history.results.filter((result) => result.date !== date)
      }
    },
  },
})
