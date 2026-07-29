<script setup>
import { RouterLink } from 'vue-router'
</script>

<template>
  <header class="border-b border-slate-200 bg-white/80 backdrop-blur dark:border-slate-800 dark:bg-slate-950/80">
    <div class="mx-auto flex max-w-4xl flex-wrap items-center justify-between gap-3 px-6 py-4">
      <RouterLink to="/" class="text-base font-semibold text-slate-900 dark:text-slate-100">
        Weather <span class="text-slate-400 dark:text-slate-600">·</span> Air Pollution Tracker
      </RouterLink>
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
    </div>
  </header>
</template>
