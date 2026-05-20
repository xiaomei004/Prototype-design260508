<script setup>
import { computed, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useUserStore } from "../stores/user";

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const mode = ref("login");
const submitting = ref(false);
const errorMessage = ref("");

const loginForm = ref({
  username: "",
  password: ""
});

const registerForm = ref({
  username: "",
  nickname: "",
  password: ""
});

const isLoginMode = computed(() => mode.value === "login");

function switchMode(nextMode) {
  mode.value = nextMode;
  errorMessage.value = "";
}

async function submitLogin() {
  submitting.value = true;
  errorMessage.value = "";
  try {
    await userStore.login({
      username: loginForm.value.username.trim(),
      password: loginForm.value.password
    });
    await userStore.fetchCurrentUser();
    router.replace((route.query.redirect && String(route.query.redirect)) || { name: "discover" });
  } catch (error) {
    errorMessage.value = error?.message || "登录失败，请检查用户名和密码。";
  } finally {
    submitting.value = false;
  }
}

async function submitRegister() {
  submitting.value = true;
  errorMessage.value = "";
  try {
    await userStore.register({
      username: registerForm.value.username.trim(),
      nickname: registerForm.value.nickname.trim(),
      password: registerForm.value.password
    });
    loginForm.value.username = registerForm.value.username.trim();
    loginForm.value.password = registerForm.value.password;
    switchMode("login");
  } catch (error) {
    errorMessage.value = error?.message || "注册失败，请稍后再试。";
  } finally {
    submitting.value = false;
  }
}
</script>

<template>
  <section class="auth-shell">
    <div class="auth-card">
      <div class="auth-copy">
        <p class="eyebrow">湖工大校园守护</p>
        <h1>校园流浪动物管理系统</h1>
        <p class="auth-description">先登录，再进入发现广场、图鉴、随手拍和个人中心。</p>
      </div>

      <div class="auth-switch" role="tablist" aria-label="登录与注册">
        <button :class="{ active: isLoginMode }" type="button" @click="switchMode('login')">登录</button>
        <button :class="{ active: !isLoginMode }" type="button" @click="switchMode('register')">注册</button>
      </div>

      <form v-if="isLoginMode" class="auth-form" @submit.prevent="submitLogin">
        <label>
          用户名
          <input v-model="loginForm.username" required maxlength="20" placeholder="请输入用户名">
        </label>
        <label>
          密码
          <input v-model="loginForm.password" type="password" required maxlength="32" placeholder="请输入密码">
        </label>
        <button class="solid-button auth-submit" :disabled="submitting" type="submit">
          {{ submitting ? "登录中..." : "登录" }}
        </button>
      </form>

      <form v-else class="auth-form" @submit.prevent="submitRegister">
        <label>
          用户名
          <input v-model="registerForm.username" required maxlength="20" placeholder="请输入用户名">
        </label>
        <label>
          昵称
          <input v-model="registerForm.nickname" required maxlength="20" placeholder="请输入昵称">
        </label>
        <label>
          密码
          <input v-model="registerForm.password" type="password" required maxlength="32" placeholder="请输入密码">
        </label>
        <button class="solid-button auth-submit" :disabled="submitting" type="submit">
          {{ submitting ? "注册中..." : "注册" }}
        </button>
      </form>

      <div v-if="errorMessage" class="auth-error">{{ errorMessage }}</div>

      <div class="auth-note">
        演示账号：student01 / 123456
      </div>
    </div>
  </section>
</template>
