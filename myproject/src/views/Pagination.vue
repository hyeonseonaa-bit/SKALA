<template>
  <div>
    <!-- 페이지네이션 -->
    <div class="pagination">
      <button
        @click="$emit('change-page', currentPage - 1)"
        :disabled="currentPage === 1 || loading"
        class="btn btn-page"
      >
        Prev
      </button>

      <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>

      <button
        @click="$emit('change-page', currentPage + 1)"
        :disabled="currentPage === totalPages || loading"
        class="btn btn-page"
      >
        Next
      </button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  currentPage: Number,
  totalPages: Number,
  loading: Boolean,
});

defineEmits(["change-page"]);
</script>
