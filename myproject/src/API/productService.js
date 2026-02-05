import axios from "axios";

const API_URL = "http://localhost:3000/products";

export default {
  // GET - 페이징 조회
  getProducts(page = 1, limit = 5) {
    return axios.get(`${API_URL}?_page=${page}&_limit=${limit}`);
  },

  // GET - 전체 조회 (통계용)
  getAllProducts() {
    return axios.get(API_URL);
  },

  // POST - 새 상품 생성
  createProduct(product) {
    return axios.post(API_URL, product);
  },

  // PUT - 기존 상품 수정
  updateProduct(id, product) {
    return axios.put(`${API_URL}/${id}`, product);
  },

  // DELETE - 삭제
  deleteProduct(id) {
    return axios.delete(`${API_URL}/${id}`);
  },
};
