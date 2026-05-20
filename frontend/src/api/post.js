import request from "../utils/request";

export function getPostList(params) {
  return request({
    url: "/posts",
    method: "get",
    params
  });
}

export function createPost(data) {
  return request({
    url: "/posts",
    method: "post",
    data
  });
}

export function updatePost(id, data) {
  return request({
    url: `/posts/${id}`,
    method: "put",
    data
  });
}

export function deletePost(id) {
  return request({
    url: `/posts/${id}`,
    method: "delete"
  });
}
