import { defineStore } from 'pinia'
import { getGeocode } from '../api/geocode'
import { extractErrorMessage } from '../api/errors'

export const useGeocodeStore = defineStore('geocode', {
  state: () => ({
    result: null,
    loading: false,
    error: null,
  }),

  actions: {
    async lookup(city) {
      this.loading = true
      this.error = null
      this.result = null
      try {
        this.result = await getGeocode(city)
      } catch (err) {
        this.error = extractErrorMessage(err)
      } finally {
        this.loading = false
      }
    },
  },
})
