<script setup></script>

<template>
  <nav class="navigation">
    <ul class="nav-menu">
      <li>
        <p>SKALA Market</p>
      </li>
      <li>
        <RouterLink to="/" class="nav-link">HOME</RouterLink>
      </li>
      <li>
        <RouterLink to="/about" class="nav-link">ABOUT</RouterLink>
      </li>
      <li>
        <RouterLink to="/ProductManagement" class="nav-link"
          >Product Management System</RouterLink
        >
      </li>
    </ul>
  </nav>
  <RouterView />
</template>

<style scoped>
.navigation {
  background: #2c3e50;
  color: white;
  padding: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
}

.logo a {
  font-size: 1.5rem;
  font-weight: bold;
  color: #42b983;
  text-decoration: none;
  transition: color 0.3s;
}

.logo a:hover {
  color: #35a372;
}

.nav-menu {
  display: flex;
  list-style: none;
  margin: 0;
  padding: 0;
  gap: 2rem;
}

.nav-link {
  color: white;
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition:
    background 0.3s,
    color 0.3s;
  display: block;
}

.nav-link:hover {
  background: rgba(255, 255, 255, 0.1);
}

.nav-link.router-link-active {
  background: #42b983;
  color: white;
}

@media (max-width: 768px) {
  .nav-container {
    flex-direction: column;
    gap: 1rem;
  }

  .nav-menu {
    flex-wrap: wrap;
    justify-content: center;
    gap: 1rem;
  }
}
</style>
