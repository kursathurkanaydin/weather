import apiClient from './client'

export function getGeocode(city) {
  return apiClient.get('/geocode', { params: { city } }).then((res) => res.data)
}
