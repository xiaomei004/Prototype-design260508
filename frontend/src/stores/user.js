import { computed, ref } from "vue";
import { defineStore } from "pinia";
import { login as loginApi, register as registerApi } from "../api/auth";
import { getCurrentUser } from "../api/user";
import { clearAuthState, getStoredUserInfo, getToken, setStoredUserInfo, setToken } from "../utils/auth";

export const useUserStore = defineStore("user", () => {
  const token = ref(getToken());
  const userInfo = ref(getStoredUserInfo());
  const bootstrapping = ref(false);
  const bootstrapError = ref("");

  const isLoggedIn = computed(() => Boolean(token.value));

  async function login(payload) {
    const data = await loginApi(payload);
    token.value = data.token;
    userInfo.value = data.userInfo || null;
    if (data.token) {
      setToken(data.token);
    }
    if (data.userInfo) {
      setStoredUserInfo(data.userInfo);
    }
    bootstrapError.value = "";
    return data;
  }

  async function register(payload) {
    return registerApi(payload);
  }

  async function fetchCurrentUser() {
    const data = await getCurrentUser();
    userInfo.value = data;
    setStoredUserInfo(data);
    return data;
  }

  async function bootstrapSession() {
    bootstrapping.value = true;
    bootstrapError.value = "";
    try {
      if (!token.value) {
        return null;
      }

      return await fetchCurrentUser();
    } catch (error) {
      logout();
      bootstrapError.value = "登录状态已失效，请重新登录。";
      throw error;
    } finally {
      bootstrapping.value = false;
    }
  }

  function logout() {
    token.value = "";
    userInfo.value = null;
    clearAuthState();
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    bootstrapping,
    bootstrapError,
    login,
    register,
    fetchCurrentUser,
    bootstrapSession,
    logout
  };
});
