function getApiOrigin() {
  const baseUrl = import.meta.env.VITE_API_BASE_URL || "";
  if (baseUrl.startsWith("http://") || baseUrl.startsWith("https://")) {
    return new URL(baseUrl).origin;
  }
  return "";
}

export function resolveBackendAssetUrl(url) {
  if (!url) {
    return "";
  }

  if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("data:")) {
    return url;
  }

  const origin = getApiOrigin();
  if (!origin) {
    return url;
  }

  if (url.startsWith("/")) {
    return `${origin}${url}`;
  }

  return `${origin}/${url}`;
}
