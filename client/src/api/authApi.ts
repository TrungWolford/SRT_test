import axiosInstance from '../libs/axios';
import { API } from '../config/constants';
import { ApiResponse, UserInfo } from '../types';

export interface LoginPayload {
  accountName: string;
  password: string;
}

export interface RegisterPayload {
  accountName: string;
  fullName: string;
  email: string;
  password: string;
}

export interface LoginResponseData {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: UserInfo;
}

export interface RegisterResponseData {
  message: string;
  userName: string;
  accountName: string;
  fullName: string;
}

export const authApi = {
  login: async (payload: LoginPayload): Promise<ApiResponse<LoginResponseData>> => {
    const response = await axiosInstance.post(API.LOGIN, payload);
    return response.data;
  },

  register: async (payload: RegisterPayload): Promise<ApiResponse<RegisterResponseData>> => {
    const response = await axiosInstance.post(API.REGISTER, payload);
    return response.data;
  },

  logout: async (refreshToken: string): Promise<ApiResponse<null>> => {
    const response = await axiosInstance.post(API.LOGOUT, { refreshToken });
    return response.data;
  },

  refresh: async (refreshToken: string): Promise<ApiResponse<LoginResponseData>> => {
    const response = await axiosInstance.post(`${API.REFRESH}?refreshToken=${encodeURIComponent(refreshToken)}`);
    return response.data;
  }
};
