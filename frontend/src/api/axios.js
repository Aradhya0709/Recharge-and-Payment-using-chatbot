import axios from "axios";

const API = axios.create({
  // 🎯 यह लाइन .env फ़ाइल से अपने आप लाइव या लोकल URL उठा लेगी
  baseURL: import.meta.env.VITE_API_BASE_URL || "", 
  headers: {
    "Content-Type": "application/json",
  },
});

// Request Interceptor: हर रिक्वेस्ट के साथ JWT टोकन भेजने के लिए
API.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("paybot_token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response Interceptor: एरर्स को कंसोल में लॉग करने के लिए
API.interceptors.response.use(
  (res) => res,
  (err) => {
    console.error("API Error:", err.message);
    return Promise.reject(err);
  }
);

export default API;