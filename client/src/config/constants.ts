// Production URL hoặc localhost cho development
const PRODUCTION_API_URL = "http://localhost:8080";
const DEV_API_URL = "http://localhost:8080";

// Tự động chọn URL dựa trên môi trường
const BASE_URL = import.meta.env.VITE_API_BASE_URL ||
  (import.meta.env.PROD ? PRODUCTION_API_URL : DEV_API_URL);

export const CONFIG = {
  API_GATEWAY: BASE_URL,
};

export const API = {
  // Auth
  LOGIN: '/api/auth/login',
  REGISTER: '/api/auth/register',
  LOGOUT: '/api/auth/logout',
  REFRESH: '/api/auth/refresh',

  // Tasks
  TASKS: '/api/tasks',
  TASK_BY_ID: (taskId: string) => `/api/tasks/${taskId}`,
  TASK_COMPLETION: (taskId: string) => `/api/tasks/${taskId}/completion`,
  TASK_RESTORE: (taskId: string) => `/api/tasks/${taskId}/restore`,
} as const;

export const STORAGE_KEYS = {
  ACCESS_TOKEN: 'access_token',
  REFRESH_TOKEN: 'refresh_token',
  USER_PROFILE: 'user_profile',
} as const;

export const ROUTES = {
  HOME: '/',
  LOGIN: '/login',
  REGISTER: '/register',
} as const;

export default {
  CONFIG,
  API,
  STORAGE_KEYS,
  ROUTES,
};
