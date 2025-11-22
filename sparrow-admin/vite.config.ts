import eslint from "vite-plugin-eslint2";
import { reactRouter } from "@react-router/dev/vite";
import tailwindcss from "@tailwindcss/vite";
import { defineConfig } from "vite";
import tsconfigPaths from "vite-tsconfig-paths";

export default defineConfig({
  plugins: [eslint({fix: true}), tailwindcss(), reactRouter(), tsconfigPaths()],
});
