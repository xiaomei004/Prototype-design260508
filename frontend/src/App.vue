<script setup>
import { computed, onMounted } from "vue";
import { storeToRefs } from "pinia";
import { RouterView, useRoute } from "vue-router";
import BottomNav from "./components/BottomNav.vue";
import { useUserStore } from "./stores/user";

const route = useRoute();
const userStore = useUserStore();
const { bootstrapping, bootstrapError } = storeToRefs(userStore);

const isAuthLayout = computed(() => route.meta.layout === "auth");
const pageTitle = computed(() => route.meta.title || "校园流浪动物管理系统");

onMounted(async () => {
  try {
    await userStore.bootstrapSession();
  } catch (error) {
    console.warn("用户初始化失败", error);
  }
});
</script>

<template>
  <RouterView v-if="isAuthLayout" />

  <main v-else class="phone-shell" aria-label="校园流浪动物管理系统移动端原型">
    <section class="app-screen">
      <header class="top-bar">
        <div>
          <p class="eyebrow">湖工大校园守护</p>
          <h1>{{ pageTitle }}</h1>
        </div>
        <button class="icon-button" type="button" aria-label="通知">
          <span class="bell-icon"></span>
          <span class="notice-dot"></span>
        </button>
      </header>

      <div v-if="bootstrapping" class="page">
        <div class="empty-state">正在校验登录状态...</div>
      </div>
      <div v-else-if="bootstrapError" class="page">
        <div class="empty-state">{{ bootstrapError }}</div>
      </div>
      <RouterView v-else />
      <BottomNav />
    </section>
  </main>
</template>
