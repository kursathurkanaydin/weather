import { createRouter, createWebHistory } from 'vue-router'
import RecordsView from '../views/RecordsView.vue'
import HistoryView from '../views/HistoryView.vue'
import GeocodeView from '../views/GeocodeView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'records', component: RecordsView },
    { path: '/history', name: 'history', component: HistoryView },
    { path: '/geocode', name: 'geocode', component: GeocodeView },
  ],
})

export default router
