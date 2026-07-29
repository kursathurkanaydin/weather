import apiClient from './client'

export function getAllAirPollutions({ page = 0, size = 10, sortBy = 'id', ascending = true } = {}) {
  return apiClient
    .get('/air-pollution', { params: { page, size, sortBy, ascending } })
    .then((res) => res.data)
}

export function getAirPollutionsByCity(city) {
  return apiClient.get(`/air-pollution/city/${city}`).then((res) => res.data)
}

export function getAirPollutionsByCityAndDateRange(city, startDate, endDate) {
  return apiClient
    .get(`/air-pollution/city/${city}/range-date`, { params: { startDate, endDate } })
    .then((res) => res.data)
}

export function fetchAirPollutionHistory(city, startDate, endDate) {
  return apiClient
    .get('/air-pollution/history', { params: { city, startDate, endDate } })
    .then((res) => res.data)
}

export function deleteAirPollutionById(id) {
  return apiClient.delete(`/air-pollution/${id}`)
}

export function deleteAirPollutionByCityAndDate(city, date) {
  return apiClient.delete(`/air-pollution/${city}/${date}`)
}
