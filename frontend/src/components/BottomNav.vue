<script setup>
import { computed } from "vue";
import { useRoute, useRouter } from "vue-router";

const router = useRouter();
const route = useRoute();

const navItems = [
  { name: "discover", label: "发现", icon: "home" },
  { name: "camera", label: "随手拍", icon: "scan" },
  { name: "album", label: "图鉴", icon: "paw" },
  { name: "profile", label: "我的", icon: "user" }
];

const currentName = computed(() => route.name);

function goTo(name) {
  router.push({ name });
}
</script>

<template>
  <nav class="bottom-nav" aria-label="底部导航">
    <button
      v-for="item in navItems"
      :key="item.name"
      :class="{ active: currentName === item.name }"
      type="button"
      @click="goTo(item.name)"
    >
      <span class="nav-icon" :class="item.icon"></span>
      {{ item.label }}
    </button>
  </nav>
</template>
