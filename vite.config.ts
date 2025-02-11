// vite.config.ts
import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: [
      { find: "@", replacement: "/src" }, // `@`을 `src` 폴더로 매핑
    ],
  },
  server: {
    proxy: {
      "/api": {
        target: "http://3.38.114.211:8080",
        changeOrigin: true,
        secure: false,
        headers: {
          credentials: "include",
        },
      },
      "/ws": {
        target: "http://3.38.114.211:8080",
        changeOrigin: true,
        ws: true,
        secure: false,
        headers: {
          credentials: "include",
        },
      },
    },
  },
});
