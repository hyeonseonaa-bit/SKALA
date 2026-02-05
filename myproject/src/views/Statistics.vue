<template>
  <div class="statistics">
    <h3>📊 통계</h3>
    <div class="stats-grid">
      <div class="stat-item">
        <span class="stat-label">총 상품 수</span>
        <span class="stat-value">{{ allProducts.length }}개</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">표시 중인 상품</span>
        <span class="stat-value">{{ filteredProducts.length }}개</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">총 재고 수량</span>
        <span class="stat-value">{{ totalStock }}개</span>
      </div>
      <div class="stat-item highlight">
        <span class="stat-label">총 재고 가치</span>
        <span class="stat-value">{{ formatPrice(totalInventoryValue) }}원</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">평균 상품 가격</span>
        <span class="stat-value">{{ formatPrice(averagePrice) }}원</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">할인 상품</span>
        <span class="stat-value">{{ discountProducts.length }}개</span>
      </div>
      <div class="stat-item warning">
        <span class="stat-label">재고 부족 상품</span>
        <span class="stat-value">{{ lowStockProducts.length }}개</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">총 할인 금액</span>
        <span class="stat-value">{{ formatPrice(totalDiscountAmount) }}원</span>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  allProducts: Array,
  filteredProducts: Array,
  totalStock: Number,
  totalInventoryValue: Number,
  averagePrice: Number,
  discountProducts: Array,
  lowStockProducts: Array,
  totalDiscountAmount: Number,
});

function formatPrice(price) {
  const num = Number(price);
  return Number.isFinite(num) ? num.toLocaleString("ko-KR") : "0";
}
</script>
