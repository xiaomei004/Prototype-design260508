import axios from "axios";
import { clearAuthState, getToken } from "./auth";

const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000
});

function redirectToLogin() {
  if (window.location.hash !== "#/auth") {
    window.location.hash = "#/auth";
  }
}

service.interceptors.request.use(
  (config) => {
    const token = getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

service.interceptors.response.use(
  (response) => {
    const payload = response.data;
    if (payload && typeof payload.code !== "undefined") {
      if (payload.code === 200) {
        return payload.data;
      }

      if (payload.code === 401) {
        clearAuthState();
        redirectToLogin();
      }

      return Promise.reject(new Error(payload.message || "请求失败"));
    }

    return payload;
  },
  (error) => {
    const status = error.response?.status;
    if (status === 401) {
      clearAuthState();
      redirectToLogin();
    }

    return Promise.reject(error);
  }
);

export default service;
