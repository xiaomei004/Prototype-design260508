import request from "../utils/request";

export function scanAnimal(data) {
  return request({
    url: "/scan",
    method: "post",
    data
  });
}
