<template>
  <div class="search-section">
    <h2>🔍 상품 검색</h2>
    <div class="search-controls">
      <input
        :value="searchKeyword"
        @input="$emit('update:searchKeyword', $event.target.value)"
        @keyup.enter="$emit('search')"
        placeholder="상품명 또는 카테고리로 검색..."
        class="input search-input"
      />
      <button @click="$emit('search')" class="btn btn-primary">검색</button>
      <button
        @click="$emit('reset')"
        class="btn btn-reset"
        v-if="searchKeyword"
      >
        초기화
      </button>
    </div>
    <div v-if="searchKeyword" class="search-result-info">
      🔎 검색어: <strong>"{{ searchKeyword }}"</strong> -
      {{ filteredCount }}개의 상품 발견
    </div>
  </div>
</template>

<script setup>
defineProps({
  searchKeyword: String,
  filteredCount: Number,
});

defineEmits(["update:searchKeyword", "search", "reset"]);
</script>
