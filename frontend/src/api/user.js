import request from "../utils/request";

export function getCurrentUser() {
  return request({
    url: "/user/me",
    method: "get"
  });
}

export function getMyAnimals() {
  return request({
    url: "/user/animals",
    method: "get"
  });
}

export function getMyPosts() {
  return request({
    url: "/user/posts",
    method: "get"
  });
}

export function getMyScans() {
  return request({
    url: "/user/scans",
    method: "get"
  });
}
