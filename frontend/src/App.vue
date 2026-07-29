<script setup>
import NavBar from './components/NavBar.vue'
</script>

<template>
  <NavBar />
  <main class="page">
    <RouterView />
  </main>
</template>

<style scoped>
.page {
  max-width: 960px;
  margin: 0 auto;
  padding: 1.5rem;
}
</style>
