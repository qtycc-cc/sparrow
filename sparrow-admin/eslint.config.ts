import js from "@eslint/js";
import globals from "globals";
import tseslint from "typescript-eslint";
import pluginReact from "eslint-plugin-react";
import { defineConfig, globalIgnores } from "eslint/config";
import stylistic from "@stylistic/eslint-plugin";

export default defineConfig([
  {
    "settings": {
      "react": {
        "version": "detect"
      }
    }
  },
  {
    files: ["**/*.{js,mjs,cjs,ts,mts,cts,jsx,tsx}"],
    plugins: { js, pluginReact, "@stylistic": stylistic },
    extends: ["js/recommended"],
    languageOptions: { globals: globals.browser },
    rules: {
      "@stylistic/indent": ["error", 2],
      "@stylistic/quotes": ["error", "double"],
      "@stylistic/semi": ["error", "always"],
    },
  },
  tseslint.configs.recommended,
  pluginReact.configs.flat.recommended,
  pluginReact.configs.flat["jsx-runtime"],
  globalIgnores([
    "**/node_modules/**",
    "**/dist/**",
    "**/build/**",
    "**/.react-router/**",
    "app/components/ui/**"
  ]),
]);