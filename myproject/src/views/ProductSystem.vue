<template>
  <div class="container">
    <h2>🧙상품 관리 시스템🧙</h2>
    <p>
      관리자 이름:
      <input v-model="userName" placeholder="이름을 입력하세요" class="input" />
      <button @click="showName" class="btn btn-primary">확인</button>
    </p>
    <div v-if="message" class="welcome-message">
      안녕하세요 {{ message }}님! 상품 관리 시스템을 시작합니다.
    </div>

    <div class="search-section">
      <h2>🔍 상품 검색</h2>
      <div class="search-controls">
        <input
          v-model="searchKeyword"
          @keyup.enter="handleSearch"
          placeholder="상품명 또는 카테고리로 검색..."
          class="input search-input"
        />
        <button @click="handleSearch" class="btn btn-primary">검색</button>
        <button @click="resetSearch" class="btn btn-reset" v-if="searchKeyword">
          초기화
        </button>
      </div>
      <div v-if="searchKeyword" class="search-result-info">
        🔎 검색어: <strong>"{{ searchKeyword }}"</strong> -
        {{ filteredProducts.length }}개의 상품 발견
      </div>
    </div>

    <div class="add-product-section">
      <h2>{{ isEditing ? "✏️ 상품 수정" : "➕ 새 상품 추가" }}</h2>
      <div class="form-grid">
        <input v-model="newProduct.name" placeholder="상품명" class="input" />

        <select v-model="newProduct.category" class="input">
          <option value="">카테고리 선택</option>
          <option value="전자제품">전자제품</option>
          <option value="의류">의류</option>
          <option value="식품">식품</option>
          <option value="도서">도서</option>
          <option value="기타">기타</option>
        </select>

        <input
          v-model.number="newProduct.stock"
          type="number"
          placeholder="재고를 입력하세요"
          class="input"
          min="0"
        />

        <input
          v-model.number="newProduct.basePrice"
          type="number"
          placeholder="기본 가격을 입력하세요"
          class="input"
          min="0"
        />

        <input
          v-model.number="newProduct.discount"
          type="number"
          placeholder="할인율(%)을 입력하세요"
          class="input"
          min="0"
          max="100"
        />

        <input
          v-model.trim="newProduct.description"
          type="text"
          placeholder="상품 설명을 입력하세요"
          class="input full-width"
        />

        <!-- 가격 미리보기 -->
        <div class="price-preview full-width">
          <div class="price-calc">
            <span class="label">기본 가격:</span>
            <span class="value"
              >{{ formatPrice(newProduct.basePrice || 0) }}원</span
            >
          </div>
          <div class="price-calc" v-if="newProduct.discount > 0">
            <span class="label">할인율:</span>
            <span class="value discount">-{{ newProduct.discount }}%</span>
          </div>
          <div class="price-calc" v-if="newProduct.discount > 0">
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
            @click="isEditing ? updateProduct() : addProduct()"
            class="btn btn-add"
            :disabled="loading"
          >
            {{ isEditing ? "수정 완료" : "상품 추가" }}
          </button>
          <button
            v-if="isEditing"
            @click="cancelEdit"
            class="btn btn-secondary"
          >
            취소
          </button>
        </div>
      </div>
    </div>

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
          @click="changeFilter('all')"
          :class="['btn-filter', { active: filter === 'all' }]"
        >
          전체 ({{ allProducts.length }})
        </button>
        <button
          @click="changeFilter('inStock')"
          :class="['btn-filter', { active: filter === 'inStock' }]"
        >
          재고 있음 ({{ inStockProducts.length }})
        </button>
        <button
          @click="changeFilter('outOfStock')"
          :class="['btn-filter', { active: filter === 'outOfStock' }]"
        >
          품절 ({{ outOfStockProducts.length }})
        </button>
        <button
          @click="changeFilter('lowStock')"
          :class="['btn-filter', { active: filter === 'lowStock' }]"
        >
          재고 부족 ({{ lowStockProducts.length }})
        </button>
        <button
          @click="changeFilter('discount')"
          :class="['btn-filter', { active: filter === 'discount' }]"
        >
          할인 상품 ({{ discountProducts.length }})
        </button>
      </div>

      <div v-if="paginatedProducts.length === 0" class="empty-state">
        <p>😔 표시할 상품이 없습니다.</p>
        <button
          v-if="searchKeyword"
          @click="resetSearch"
          class="btn btn-primary"
        >
          전체 상품 보기
        </button>
      </div>

      <div
        v-for="product in paginatedProducts"
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
                  {{ formatPrice(product.basePrice - product.finalPrice) }}원
                  할인
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
                @click="adjustStock(product.id, 1)"
                class="btn btn-stock btn-increase"
                :disabled="loading"
              >
                +1
              </button>
              <button
                @click="adjustStock(product.id, 10)"
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
              @click="editProduct(product)"
              class="btn btn-edit"
              :disabled="isEditing || loading"
            >
              수정
            </button>
            <button
              @click="deleteProduct(product.id)"
              class="btn btn-delete"
              :disabled="loading"
            >
              삭제
            </button>
          </div>
        </div>
      </div>

      <div v-if="totalPages > 1" class="pagination">
        <button
          @click="changePage(currentPage - 1)"
          :disabled="currentPage === 1 || loading"
          class="btn btn-page"
        >
          이전
        </button>

        <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>

        <button
          @click="changePage(currentPage + 1)"
          :disabled="currentPage === totalPages || loading"
          class="btn btn-page"
        >
          다음
        </button>
      </div>

      <div v-if="allProducts.length > 0" class="statistics">
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
            <span class="stat-value"
              >{{ formatPrice(totalInventoryValue) }}원</span
            >
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
            <span class="stat-value"
              >{{ formatPrice(totalDiscountAmount) }}원</span
            >
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from "vue";
import productService from "../API/productService";

const userName = ref("");
const message = ref("");
const allProducts = ref([]);
const filter = ref("all");
const loading = ref(false);

const currentPage = ref(1);
const itemsPerPage = 5;

const searchKeyword = ref("");

const isEditing = ref(false);
const editingId = ref(null);

const newProduct = reactive({
  name: "",
  category: "",
  basePrice: "",
  discount: "",
  stock: "",
  description: "",
});

onMounted(() => {
  fetchProducts();
});

// 가격 계산 (입력 폼용)
const calculatedDiscount = computed(() => {
  const base = Number(newProduct.basePrice) || 0;
  const disc = Number(newProduct.discount) || 0;
  return base * (disc / 100);
});

const calculatedFinalPrice = computed(() => {
  const base = Number(newProduct.basePrice) || 0;
  return Math.max(0, base - calculatedDiscount.value);
});

async function fetchProducts() {
  loading.value = true;
  try {
    const response = await productService.getProducts(1, 100);
    // 기존 데이터에 할인 정보가 없으면 추가
    allProducts.value = response.data.map((product) => ({
      ...product,
      basePrice: product.basePrice || product.price || 0,
      discount: product.discount || 0,
      finalPrice: product.finalPrice || product.price || 0,
    }));
    console.log("✅ 상품 조회 성공:", allProducts.value.length, "개");
  } catch (error) {
    console.error("❌ 상품 조회 실패:", error);
    alert(
      "상품을 불러오는데 실패했습니다. JSON Server가 실행 중인지 확인하세요.",
    );
  } finally {
    loading.value = false;
  }
}

function showName() {
  if (userName.value.trim()) {
    message.value = userName.value;
  }
}

async function addProduct() {
  if (!newProduct.name.trim() || !newProduct.category) {
    alert("상품명과 카테고리는 필수 입력 항목입니다.");
    return;
  }

  const basePrice = Number(newProduct.basePrice) || 0;
  const discount = Number(newProduct.discount) || 0;

  if (basePrice < 0 || Number(newProduct.stock) < 0) {
    alert("가격과 재고는 0 이상이어야 합니다.");
    return;
  }

  if (discount < 0 || discount > 100) {
    alert("할인율은 0~100 사이여야 합니다.");
    return;
  }

  loading.value = true;
  try {
    const finalPrice = Math.max(0, basePrice - (basePrice * discount) / 100);

    const productData = {
      name: newProduct.name.trim(),
      category: newProduct.category,
      basePrice: basePrice,
      discount: discount,
      finalPrice: finalPrice,
      price: finalPrice, // 하위 호환성
      stock: Number(newProduct.stock) || 0,
      description: newProduct.description.trim(),
    };

    await productService.createProduct(productData);
    console.log("✅ 상품 추가 성공");
    alert("상품이 등록되었습니다!");

    resetForm();
    await fetchProducts();
    currentPage.value = 1;
  } catch (error) {
    console.error("❌ 상품 추가 실패:", error);
    alert("상품 등록에 실패했습니다.");
  } finally {
    loading.value = false;
  }
}

function editProduct(product) {
  isEditing.value = true;
  editingId.value = product.id;

  Object.assign(newProduct, {
    name: product.name,
    category: product.category,
    basePrice: product.basePrice,
    discount: product.discount,
    stock: product.stock,
    description: product.description,
  });

  window.scrollTo({ top: 0, behavior: "smooth" });
}

async function updateProduct() {
  if (!newProduct.name.trim() || !newProduct.category) {
    alert("상품명과 카테고리는 필수 입력 항목입니다.");
    return;
  }

  const basePrice = Number(newProduct.basePrice) || 0;
  const discount = Number(newProduct.discount) || 0;

  if (basePrice < 0 || Number(newProduct.stock) < 0) {
    alert("가격과 재고는 0 이상이어야 합니다.");
    return;
  }

  if (discount < 0 || discount > 100) {
    alert("할인율은 0~100 사이여야 합니다.");
    return;
  }

  loading.value = true;
  try {
    const finalPrice = Math.max(0, basePrice - (basePrice * discount) / 100);

    const productData = {
      name: newProduct.name.trim(),
      category: newProduct.category,
      basePrice: basePrice,
      discount: discount,
      finalPrice: finalPrice,
      price: finalPrice,
      stock: Number(newProduct.stock) || 0,
      description: newProduct.description.trim(),
    };

    await productService.updateProduct(editingId.value, productData);
    console.log("✅ 상품 수정 성공");
    alert("상품이 수정되었습니다!");

    resetForm();
    await fetchProducts();
  } catch (error) {
    console.error("❌ 상품 수정 실패:", error);
    alert("상품 수정에 실패했습니다.");
  } finally {
    loading.value = false;
  }
}

function cancelEdit() {
  resetForm();
}

function resetForm() {
  Object.assign(newProduct, {
    name: "",
    category: "",
    basePrice: "",
    discount: "",
    stock: "",
    description: "",
  });
  isEditing.value = false;
  editingId.value = null;
}

function adjustStock(productId, amount) {
  const product = allProducts.value.find((p) => p.id === productId);
  if (product) {
    const newStock = product.stock + amount;
    if (newStock >= 0) {
      product.stock = newStock;
      updateStockOnServer(productId, newStock);
    } else {
      alert("재고는 0 미만이 될 수 없습니다.");
    }
  }
}

async function updateStockOnServer(productId, newStock) {
  const product = allProducts.value.find((p) => p.id === productId);
  if (!product) return;

  try {
    const productData = {
      name: product.name,
      category: product.category,
      basePrice: product.basePrice,
      discount: product.discount,
      finalPrice: product.finalPrice,
      price: product.finalPrice,
      stock: newStock,
      description: product.description,
    };

    await productService.updateProduct(productId, productData);
    console.log("✅ 재고 업데이트 성공");
  } catch (error) {
    console.error("❌ 재고 업데이트 실패:", error);
    await fetchProducts();
  }
}

async function deleteProduct(id) {
  if (!confirm("정말 삭제하시겠습니까?")) return;

  loading.value = true;
  try {
    await productService.deleteProduct(id);
    console.log("✅ 상품 삭제 성공");
    alert("상품이 삭제되었습니다!");

    if (editingId.value === id) cancelEdit();
    await fetchProducts();

    if (paginatedProducts.value.length === 0 && currentPage.value > 1) {
      currentPage.value--;
    }
  } catch (error) {
    console.error("❌ 상품 삭제 실패:", error);
    alert("상품 삭제에 실패했습니다.");
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  if (searchKeyword.value.trim()) {
    currentPage.value = 1;
    console.log("✅ 검색:", searchKeyword.value);
  }
}

function resetSearch() {
  searchKeyword.value = "";
  currentPage.value = 1;
  filter.value = "all";
  console.log("✅ 검색 초기화");
}

function changeFilter(newFilter) {
  filter.value = newFilter;
  currentPage.value = 1;
}

function changePage(page) {
  if (page < 1 || page > totalPages.value || loading.value) return;
  currentPage.value = page;
  window.scrollTo({ top: 400, behavior: "smooth" });
}

function formatPrice(price) {
  const num = Number(price);
  return Number.isFinite(num) ? num.toLocaleString("ko-KR") : "0";
}

// Computed 속성들

const filteredProducts = computed(() => {
  let products = allProducts.value;

  if (searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.toLowerCase();
    products = products.filter(
      (product) =>
        product.name.toLowerCase().includes(keyword) ||
        product.category.toLowerCase().includes(keyword) ||
        product.description.toLowerCase().includes(keyword),
    );
  }

  if (filter.value === "inStock") {
    return products.filter((p) => p.stock > 0);
  } else if (filter.value === "outOfStock") {
    return products.filter((p) => p.stock === 0);
  } else if (filter.value === "lowStock") {
    return products.filter((p) => p.stock > 0 && p.stock <= 5);
  } else if (filter.value === "discount") {
    return products.filter((p) => p.discount > 0);
  }

  return products;
});

const paginatedProducts = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return filteredProducts.value.slice(start, end);
});

const totalPages = computed(() => {
  return Math.ceil(filteredProducts.value.length / itemsPerPage);
});

const inStockProducts = computed(() => {
  return allProducts.value.filter((product) => product.stock > 0);
});

const outOfStockProducts = computed(() => {
  return allProducts.value.filter((product) => product.stock === 0);
});

const lowStockProducts = computed(() => {
  return allProducts.value.filter(
    (product) => product.stock > 0 && product.stock <= 5,
  );
});

const discountProducts = computed(() => {
  return allProducts.value.filter((product) => product.discount > 0);
});

const totalStock = computed(() => {
  return allProducts.value.reduce((total, product) => {
    return total + product.stock;
  }, 0);
});

// 총 재고 가치 (최종 가격 × 재고)
const totalInventoryValue = computed(() => {
  return allProducts.value.reduce((total, product) => {
    return total + product.finalPrice * product.stock;
  }, 0);
});

// 평균 상품 가격
const averagePrice = computed(() => {
  if (allProducts.value.length === 0) return 0;
  const totalPrice = allProducts.value.reduce(
    (sum, product) => sum + product.finalPrice,
    0,
  );
  return totalPrice / allProducts.value.length;
});

// 총 할인 금액
const totalDiscountAmount = computed(() => {
  return allProducts.value.reduce((total, product) => {
    return total + (product.basePrice - product.finalPrice);
  }, 0);
});

const lowStockWarnings = ref([]);

watch(
  () =>
    allProducts.value.map((p) => ({ id: p.id, name: p.name, stock: p.stock })),
  (newProducts) => {
    lowStockWarnings.value = newProducts.filter(
      (p) => p.stock > 0 && p.stock <= 5,
    );
  },
  { deep: true },
);

watch(totalPages, (newTotalPages) => {
  if (currentPage.value > newTotalPages && newTotalPages > 0) {
    currentPage.value = newTotalPages;
  }
});
</script>
