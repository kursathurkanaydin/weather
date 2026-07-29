<script setup>
import { RouterLink } from 'vue-router'
</script>

<template>
  <header class="navbar">
    <div class="navbar__inner">
      <RouterLink to="/" class="navbar__brand">Weather · Air Pollution Tracker</RouterLink>
      <nav class="navbar__links">
        <RouterLink to="/" class="navbar__link">Records</RouterLink>
        <RouterLink to="/history" class="navbar__link">Fetch History</RouterLink>
        <RouterLink to="/geocode" class="navbar__link">Geocode</RouterLink>
      </nav>
    </div>
  </header>
</template>

<style scoped>
.navbar {
  border-bottom: 1px solid var(--border);
  background: var(--surface);
}

.navbar__inner {
  max-width: 960px;
  margin: 0 auto;
  padding: 1rem 1.5rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.navbar__brand {
  font-weight: 600;
  font-size: 1.05rem;
  color: var(--text-h);
  text-decoration: none;
}

.navbar__links {
  display: flex;
  gap: 1.25rem;
}

.navbar__link {
  color: var(--text);
  text-decoration: none;
  font-size: 0.95rem;
  padding-bottom: 2px;
  border-bottom: 2px solid transparent;
}

.navbar__link:hover {
  color: var(--text-h);
}

.navbar__link.router-link-exact-active {
  color: var(--accent);
  border-bottom-color: var(--accent);
}
</style>
