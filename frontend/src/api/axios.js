import axios from "axios";

const API = axios.create({
  baseURL: "/api",
  headers: { "Content-Type": "application/json" },
});

API.interceptors.request.use((config) => {
  const token = localStorage.getItem("paybot_token");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

API.interceptors.response.use(
  (res) => res,
  (err) => {
    console.log("API Error:", err.message);
    return Promise.reject(err);
  }
);

export default API;