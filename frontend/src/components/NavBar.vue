<script setup>
import { ref } from 'vue'
import { RouterLink } from 'vue-router'

const isDark = ref(document.documentElement.classList.contains('dark'))

function toggleTheme() {
  isDark.value = !isDark.value
  document.documentElement.classList.toggle('dark', isDark.value)
  localStorage.setItem('theme', isDark.value ? 'dark' : 'light')
}
</script>

<template>
  <header class="border-b border-slate-200 bg-white/80 backdrop-blur dark:border-slate-800 dark:bg-slate-950/80">
    <div class="mx-auto flex max-w-4xl flex-wrap items-center justify-between gap-3 px-6 py-4">
      <RouterLink to="/" class="text-base font-semibold text-slate-900 dark:text-slate-100">
        Weather <span class="text-slate-400 dark:text-slate-600">·</span> Air Pollution Tracker
      </RouterLink>
      <div class="flex items-center gap-5">
        <nav class="flex gap-5">
          <RouterLink
            to="/"
            class="border-b-2 border-transparent pb-0.5 text-sm text-slate-500 transition-colors hover:text-slate-900 [&.router-link-exact-active]:border-blue-600 [&.router-link-exact-active]:text-blue-600 dark:text-slate-400 dark:hover:text-slate-100 dark:[&.router-link-exact-active]:border-blue-400 dark:[&.router-link-exact-active]:text-blue-400"
          >
            Records
          </RouterLink>
          <RouterLink
            to="/history"
            class="border-b-2 border-transparent pb-0.5 text-sm text-slate-500 transition-colors hover:text-slate-900 [&.router-link-exact-active]:border-blue-600 [&.router-link-exact-active]:text-blue-600 dark:text-slate-400 dark:hover:text-slate-100 dark:[&.router-link-exact-active]:border-blue-400 dark:[&.router-link-exact-active]:text-blue-400"
          >
            Fetch History
          </RouterLink>
          <RouterLink
            to="/geocode"
            class="border-b-2 border-transparent pb-0.5 text-sm text-slate-500 transition-colors hover:text-slate-900 [&.router-link-exact-active]:border-blue-600 [&.router-link-exact-active]:text-blue-600 dark:text-slate-400 dark:hover:text-slate-100 dark:[&.router-link-exact-active]:border-blue-400 dark:[&.router-link-exact-active]:text-blue-400"
          >
            Geocode
          </RouterLink>
        </nav>
        <button
          type="button"
          class="flex h-8 w-8 items-center justify-center rounded-full border border-slate-300 text-slate-500 transition-colors hover:border-blue-500 hover:text-blue-600 dark:border-slate-700 dark:text-slate-400 dark:hover:border-blue-400 dark:hover:text-blue-400"
          :aria-label="isDark ? 'Switch to light theme' : 'Switch to dark theme'"
          @click="toggleTheme"
        >
          <svg v-if="isDark" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="h-4 w-4">
            <path
              d="M12 4.5a1 1 0 0 1 1 1v1a1 1 0 1 1-2 0v-1a1 1 0 0 1 1-1Zm0 13a1 1 0 0 1 1 1v1a1 1 0 1 1-2 0v-1a1 1 0 0 1 1-1Zm7.5-6.5a1 1 0 0 1-1 1h-1a1 1 0 1 1 0-2h1a1 1 0 0 1 1 1Zm-13 0a1 1 0 0 1-1 1h-1a1 1 0 1 1 0-2h1a1 1 0 0 1 1 1Zm10.03-5.03a1 1 0 0 1 0 1.42l-.7.7a1 1 0 1 1-1.42-1.42l.7-.7a1 1 0 0 1 1.42 0Zm-9.19 9.19a1 1 0 0 1 0 1.42l-.7.7a1 1 0 1 1-1.42-1.42l.7-.7a1 1 0 0 1 1.42 0Zm9.19.7.7.7a1 1 0 0 1-1.42 1.42l-.7-.7a1 1 0 0 1 1.42-1.42ZM6.11 5.68l.7.7A1 1 0 1 1 5.4 7.8l-.7-.7a1 1 0 0 1 1.42-1.42ZM12 7a5 5 0 1 1 0 10 5 5 0 0 1 0-10Z"
            />
          </svg>
          <svg v-else xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="h-4 w-4">
            <path
              d="M20.354 15.354A9 9 0 0 1 8.646 3.646a9.003 9.003 0 1 0 11.708 11.708Z"
            />
          </svg>
        </button>
      </div>
    </div>
  </header>
</template>
