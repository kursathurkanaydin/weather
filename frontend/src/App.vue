<script setup>
import NavBar from './components/NavBar.vue'
</script>

<template>
  <div class="min-h-screen">
    <NavBar />
    <main class="mx-auto max-w-4xl px-6 py-8">
      <RouterView />
    </main>
  </div>
</template>
