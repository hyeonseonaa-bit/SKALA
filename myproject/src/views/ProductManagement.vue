<template>
  <div class="container">
    <h2>🧙상품 관리 시스템🧙</h2>

    <!-- 관리자 정보 -->
    <AdminInfo
      v-model:userName="userName"
      :message="message"
      @show-name="showName"
    />

    <!-- 검색 섹션 -->
    <ProductSearch
      v-model:searchKeyword="searchKeyword"
      :filteredCount="filteredProducts.length"
      @search="handleSearch"
      @reset="resetSearch"
    />

    <!-- 상품 추가/수정 폼 -->
    <ProductForm
      :newProduct="newProduct"
      :isEditing="isEditing"
      :loading="loading"
      @add="addProduct"
      @update="updateProduct"
      @cancel="cancelEdit"
    />

    <!-- 상품 목록 -->
    <ProductList
      :products="paginatedProducts"
      :lowStockWarnings="lowStockWarnings"
      :filter="filter"
      :allProducts="allProducts"
      :inStockProducts="inStockProducts"
      :outOfStockProducts="outOfStockProducts"
      :lowStockProducts="lowStockProducts"
      :discountProducts="discountProducts"
      :isEditing="isEditing"
      :editingId="editingId"
      :loading="loading"
      :searchKeyword="searchKeyword"
      @change-filter="changeFilter"
      @edit="editProduct"
      @delete="deleteProduct"
      @adjust-stock="adjustStock"
      @reset-search="resetSearch"
    />

    <!-- 페이징 -->
    <Pagination
      v-if="totalPages > 1"
      :currentPage="currentPage"
      :totalPages="totalPages"
      :loading="loading"
      @change-page="changePage"
    />

    <!-- 통계 -->
    <Statistics
      v-if="allProducts.length > 0"
      :allProducts="allProducts"
      :filteredProducts="filteredProducts"
      :totalStock="totalStock"
      :totalInventoryValue="totalInventoryValue"
      :averagePrice="averagePrice"
      :discountProducts="discountProducts"
      :lowStockProducts="lowStockProducts"
      :totalDiscountAmount="totalDiscountAmount"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from "vue";
import productService from "../API/productService";
import AdminInfo from "../views/AdminInfo.vue";
import ProductSearch from "./ProductSearch.vue";
import ProductForm from "../views/Productform.vue";
import ProductList from "../views/Productlist.vue";
import Pagination from "../views/Pagination.vue";
import Statistics from "../views/Statistics.vue";

const userName = ref("");
const message = ref("");
const allProducts = ref([]); // 통계용 전체 데이터
const paginatedProducts = ref([]); // 현재 페이지 데이터
const totalCount = ref(0); // 전체 상품 수
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

// 컴포넌트가 마운트될 때 상품 목록을 서버에서 가져옴
onMounted(() => {
  fetchProducts(1);
  fetchAllProducts(); // 통계용 전체 데이터
});

// 페이지별 데이터 조회 (서버 사이드 페이지네이션)
async function fetchProducts(page = 1) {
  loading.value = true;
  try {
    const response = await productService.getProducts(page, itemsPerPage);
    paginatedProducts.value = response.data.map((product) => ({
      ...product,
      basePrice: product.basePrice || product.price || 0,
      discount: product.discount || 0,
      finalPrice: product.finalPrice || product.price || 0,
    }));
    // json-server는 x-total-count 헤더에 전체 개수를 반환
    totalCount.value = parseInt(response.headers["x-total-count"]) || 0;
    console.log(`✅ 상품 조회 성공: 페이지 ${page}, ${paginatedProducts.value.length}개`);
  } catch (error) {
    console.error("❌ 상품 조회 실패:", error);
    alert(
      "상품을 불러오는데 실패했습니다. JSON Server가 실행 중인지 확인하세요.",
    );
  } finally {
    loading.value = false;
  }
}

// 통계용 전체 데이터 조회
async function fetchAllProducts() {
  try {
    const response = await productService.getAllProducts();
    allProducts.value = response.data.map((product) => ({
      ...product,
      basePrice: product.basePrice || product.price || 0,
      discount: product.discount || 0,
      finalPrice: product.finalPrice || product.price || 0,
    }));
    console.log("✅ 전체 상품 조회 성공:", allProducts.value.length, "개");
  } catch (error) {
    console.error("❌ 전체 상품 조회 실패:", error);
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
      price: finalPrice,
      stock: Number(newProduct.stock) || 0,
      description: newProduct.description.trim(),
    };
    // API 호출: 상품 생성
    await productService.createProduct(productData);
    console.log("✅ 상품 추가 성공");
    alert("상품이 등록되었습니다!");

    resetForm();
    currentPage.value = 1;
    await fetchProducts(1);
    await fetchAllProducts();
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
// API 호출: 상품 업데이트
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
    await fetchProducts(currentPage.value);
    await fetchAllProducts();
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
  const product = paginatedProducts.value.find((p) => p.id === productId);
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
  const product = paginatedProducts.value.find((p) => p.id === productId);
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
    await fetchAllProducts();
  } catch (error) {
    console.error("❌ 재고 업데이트 실패:", error);
    await fetchProducts(currentPage.value);
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
    await fetchAllProducts();

    // 현재 페이지가 비어있으면 이전 페이지로
    const newTotalPages = Math.ceil((totalCount.value - 1) / itemsPerPage);
    if (currentPage.value > newTotalPages && newTotalPages > 0) {
      currentPage.value = newTotalPages;
    }
    await fetchProducts(currentPage.value);
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

async function changePage(page) {
  if (page < 1 || page > totalPages.value || loading.value) return;
  currentPage.value = page;
  await fetchProducts(page);
  window.scrollTo({ top: 400, behavior: "smooth" });
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

const totalPages = computed(() => {
  return Math.ceil(totalCount.value / itemsPerPage);
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

const totalInventoryValue = computed(() => {
  return allProducts.value.reduce((total, product) => {
    return total + product.finalPrice * product.stock;
  }, 0);
});

const averagePrice = computed(() => {
  if (allProducts.value.length === 0) return 0;
  const totalPrice = allProducts.value.reduce(
    (sum, product) => sum + product.finalPrice,
    0,
  );
  return totalPrice / allProducts.value.length;
});

const totalDiscountAmount = computed(() => {
  return allProducts.value.reduce((total, product) => {
    return total + (product.basePrice - product.finalPrice);
  }, 0);
});

const lowStockWarnings = ref([]);

watch(
  () =>
    paginatedProducts.value.map((p) => ({ id: p.id, name: p.name, stock: p.stock })),
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
