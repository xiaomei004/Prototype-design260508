const TOKEN_KEY = "campus_animal_token";
const USER_INFO_KEY = "campus_animal_user_info";

export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || "";
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token);
}

export function removeToken() {
  localStorage.removeItem(TOKEN_KEY);
}

export function getStoredUserInfo() {
  const raw = localStorage.getItem(USER_INFO_KEY);
  if (!raw) {
    return null;
  }

  try {
    return JSON.parse(raw);
  } catch {
    return null;
  }
}

export function setStoredUserInfo(userInfo) {
  localStorage.setItem(USER_INFO_KEY, JSON.stringify(userInfo));
}

export function removeStoredUserInfo() {
  localStorage.removeItem(USER_INFO_KEY);
}

export function clearAuthState() {
  removeToken();
  removeStoredUserInfo();
}
