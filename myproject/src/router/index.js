import { createRouter, createWebHistory } from "vue-router";
import ProductManagement from "../views/ProductManagement.vue";
import Home from "../views/Home.vue";
import About from "../views/About.vue";

const routes = [
  {
    path: "/",
    name: "Home",
    component: Home,
  },
  {
    path: "/about",
    name: "About",
    component: About,
  },
  {
    path: "/ProductManagement",
    name: "ProductManagement",
    component: ProductManagement,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
