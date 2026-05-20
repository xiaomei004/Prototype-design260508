import { createRouter, createWebHashHistory } from "vue-router";
import { getToken } from "../utils/auth";
import AuthView from "../views/AuthView.vue";
import DiscoverView from "../views/DiscoverView.vue";
import CameraView from "../views/CameraView.vue";
import AlbumView from "../views/AlbumView.vue";
import AnimalDetailView from "../views/AnimalDetailView.vue";
import ProfileView from "../views/ProfileView.vue";

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: "/",
      redirect: "/discover"
    },
    {
      path: "/auth",
      name: "auth",
      component: AuthView,
      meta: { title: "登录", public: true, layout: "auth" }
    },
    {
      path: "/discover",
      name: "discover",
      component: DiscoverView,
      meta: { title: "发现广场", requiresAuth: true }
    },
    {
      path: "/camera",
      name: "camera",
      component: CameraView,
      meta: { title: "AI 随手拍", requiresAuth: true }
    },
    {
      path: "/album",
      name: "album",
      component: AlbumView,
      meta: { title: "校园图鉴", requiresAuth: true }
    },
    {
      path: "/animals/:id",
      name: "animal-detail",
      component: AnimalDetailView,
      meta: { title: "动物档案详情", requiresAuth: true }
    },
    {
      path: "/profile",
      name: "profile",
      component: ProfileView,
      meta: { title: "我的", requiresAuth: true }
    }
  ]
});

router.beforeEach((to) => {
  const token = getToken();

  if (to.meta.requiresAuth && !token) {
    return {
      name: "auth",
      query: { redirect: to.fullPath }
    };
  }

  if (to.name === "auth" && token) {
    return { name: "discover" };
  }

  return true;
});

export default router;
