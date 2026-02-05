<template>
  <div>
    <h2>🎁상품 목록</h2>

    <div v-if="lowStockWarnings.length > 0" class="warning-banner">
      <h3>⚠️ 재고 부족 경고</h3>
      <ul>
        <li v-for="warning in lowStockWarnings" :key="warning.id">
          {{ warning.name }} - 현재 재고: {{ warning.stock }}개
        </li>
      </ul>
    </div>

    <div class="filter-buttons">
      <button
        @click="$emit('change-filter', 'all')"
        :class="['btn-filter', { active: filter === 'all' }]"
      >
        전체 ({{ allProducts.length }})
      </button>
      <button
        @click="$emit('change-filter', 'inStock')"
        :class="['btn-filter', { active: filter === 'inStock' }]"
      >
        재고 있음 ({{ inStockProducts.length }})
      </button>
      <button
        @click="$emit('change-filter', 'outOfStock')"
        :class="['btn-filter', { active: filter === 'outOfStock' }]"
      >
        품절 ({{ outOfStockProducts.length }})
      </button>
      <button
        @click="$emit('change-filter', 'lowStock')"
        :class="['btn-filter', { active: filter === 'lowStock' }]"
      >
        재고 부족 ({{ lowStockProducts.length }})
      </button>
      <button
        @click="$emit('change-filter', 'discount')"
        :class="['btn-filter', { active: filter === 'discount' }]"
      >
        할인 상품 ({{ discountProducts.length }})
      </button>
    </div>

    <div v-if="products.length === 0" class="empty-state">
      <p>😔 표시할 상품이 없습니다.</p>
      <button
        v-if="searchKeyword"
        @click="$emit('reset-search')"
        class="btn btn-primary"
      >
        전체 상품 보기
      </button>
    </div>

    <div
      v-for="product in products"
      :key="product.id"
      class="product-card"
      :class="{
        'low-stock': product.stock > 0 && product.stock <= 5,
        'discount-product': product.discount > 0,
        editing: isEditing && editingId === product.id,
      }"
    >
      <div class="product-content">
        <div class="product-info">
          <h3 class="product-name">
            {{ product.name }}
            <span v-if="product.discount > 0" class="discount-badge">
              {{ product.discount }}% 할인
            </span>
          </h3>
          <div class="product-meta">
            <span class="category-badge">{{ product.category }}</span>
            <div class="price-details">
              <div class="price-row">
                <span
                  v-if="product.discount > 0"
                  class="base-price strikethrough"
                >
                  {{ formatPrice(product.basePrice) }}원
                </span>
                <span class="product-price">
                  {{ formatPrice(product.finalPrice) }}원
                </span>
              </div>
              <div v-if="product.discount > 0" class="discount-amount">
                {{ formatPrice(product.basePrice - product.finalPrice) }}원 할인
              </div>
            </div>
          </div>

          <p class="product-description">{{ product.description }}</p>

          <div class="stock-control">
            <span
              :class="[
                'stock-status',
                {
                  'out-of-stock': product.stock === 0,
                  'low-stock-text': product.stock > 0 && product.stock <= 5,
                },
              ]"
            >
              재고: {{ product.stock }}개
              <span
                v-if="product.stock > 0 && product.stock <= 5"
                class="warning-text"
              >
                (부족!)
              </span>
            </span>
            <button
              @click="$emit('adjust-stock', product.id, 1)"
              class="btn btn-stock btn-increase"
              :disabled="loading"
            >
              +1
            </button>
            <button
              @click="$emit('adjust-stock', product.id, 10)"
              class="btn btn-stock btn-increase-large"
              :disabled="loading"
            >
              +10
            </button>
          </div>

          <!-- 재고 가치 표시 -->
          <div class="inventory-value">
            재고 가치: {{ formatPrice(product.finalPrice * product.stock) }}원
          </div>
        </div>

        <div class="action-buttons">
          <button
            @click="$emit('edit', product)"
            class="btn btn-edit"
            :disabled="isEditing || loading"
          >
            수정
          </button>
          <button
            @click="$emit('delete', product.id)"
            class="btn btn-delete"
            :disabled="loading"
          >
            삭제
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  products: Array,
  lowStockWarnings: Array,
  filter: String,
  allProducts: Array,
  inStockProducts: Array,
  outOfStockProducts: Array,
  lowStockProducts: Array,
  discountProducts: Array,
  isEditing: Boolean,
  editingId: Number,
  loading: Boolean,
  searchKeyword: String,
});

defineEmits([
  "change-filter",
  "edit",
  "delete",
  "adjust-stock",
  "reset-search",
]);

function formatPrice(price) {
  const num = Number(price);
  return Number.isFinite(num) ? num.toLocaleString("ko-KR") : "0";
}
</script>
