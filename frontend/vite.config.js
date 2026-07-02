import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        // 👇 Localhost hata kar yahan apni live Railway backend URL daaliye
        target: 'https://recharge-and-payment-using-chatbot-production-665f.up.railway.app',
        changeOrigin: true,
        secure: false,
      },
    },
  },
});