<template>
  <div class="add-product-section">
    <h2>{{ isEditing ? "✏️ 상품 수정" : "➕ 새 상품 추가" }}</h2>
    <div class="form-grid">
      <input v-model="localProduct.name" placeholder="상품명" class="input" />

      <select v-model="localProduct.category" class="input">
        <option value="">카테고리 선택</option>
        <option value="전자제품">전자제품</option>
        <option value="의류">의류</option>
        <option value="식품">식품</option>
        <option value="도서">도서</option>
        <option value="기타">기타</option>
      </select>

      <input
        v-model.number="localProduct.stock"
        type="number"
        placeholder="재고를 입력하세요"
        class="input"
        min="0"
      />

      <input
        v-model.number="localProduct.basePrice"
        type="number"
        placeholder="기본 가격을 입력하세요"
        class="input"
        min="0"
      />

      <input
        v-model.number="localProduct.discount"
        type="number"
        placeholder="할인율(%)을 입력하세요"
        class="input"
        min="0"
        max="100"
      />

      <input
        v-model.trim="localProduct.description"
        type="text"
        placeholder="상품 설명을 입력하세요"
        class="input full-width"
      />

      <!-- 가격 미리보기 -->
      <div class="price-preview full-width">
        <div class="price-calc">
          <span class="label">기본 가격:</span>
          <span class="value"
            >{{ formatPrice(localProduct.basePrice || 0) }}원</span
          >
        </div>
        <div class="price-calc" v-if="localProduct.discount > 0">
          <span class="label">할인율:</span>
          <span class="value discount">-{{ localProduct.discount }}%</span>
        </div>
        <div class="price-calc" v-if="localProduct.discount > 0">
          <span class="label">할인 금액:</span>
          <span class="value discount"
            >-{{ formatPrice(calculatedDiscount) }}원</span
          >
        </div>
        <div class="price-calc final">
          <span class="label">최종 가격:</span>
          <span class="value">{{ formatPrice(calculatedFinalPrice) }}원</span>
        </div>
      </div>

      <div class="button-group">
        <button
          @click="isEditing ? $emit('update') : $emit('add')"
          class="btn btn-add"
          :disabled="loading"
        >
          {{ isEditing ? "수정 완료" : "상품 추가" }}
        </button>
        <button
          v-if="isEditing"
          @click="$emit('cancel')"
          class="btn btn-secondary"
        >
          취소
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, watch } from "vue";

const props = defineProps({
  newProduct: Object,
  isEditing: Boolean,
  loading: Boolean,
});

const emit = defineEmits(["add", "update", "cancel"]);

// newProduct를 직접 수정하기 위해 computed 사용
const localProduct = computed({
  get: () => props.newProduct,
  set: (val) => val,
});

const calculatedDiscount = computed(() => {
  const base = Number(localProduct.value.basePrice) || 0;
  const disc = Number(localProduct.value.discount) || 0;
  return base * (disc / 100);
});

const calculatedFinalPrice = computed(() => {
  const base = Number(localProduct.value.basePrice) || 0;
  return Math.max(0, base - calculatedDiscount.value);
});

function formatPrice(price) {
  const num = Number(price);
  return Number.isFinite(num) ? num.toLocaleString("ko-KR") : "0";
}
</script>
