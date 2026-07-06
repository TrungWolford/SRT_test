import axiosInstance from '../libs/axios';
import { API } from '../config/constants';
import { ApiResponse, PageResponse, Task } from '../types';

export interface CreateTaskPayload {
  title: string;
  description?: string;
  dueDate?: string | null;
  important?: boolean;
  completed?: boolean;
}

export interface UpdateTaskPayload {
  title: string;
  description?: string;
  dueDate?: string | null;
  important?: boolean;
  completed?: boolean;
  deleted?: boolean;
}

export interface GetTasksParams {
  userName: string;
  title?: string;
  status?: string;
  page?: number;
  size?: number;
}

export const taskApi = {
  getTasks: async (params: GetTasksParams): Promise<ApiResponse<PageResponse<Task>>> => {
    const response = await axiosInstance.get(API.TASKS, { params });
    return response.data;
  },

  createTask: async (payload: CreateTaskPayload): Promise<ApiResponse<Task>> => {
    const response = await axiosInstance.post(API.TASKS, payload);
    return response.data;
  },

  updateTask: async (taskId: string, payload: UpdateTaskPayload): Promise<ApiResponse<Task>> => {
    const response = await axiosInstance.put(API.TASK_BY_ID(taskId), payload);
    return response.data;
  },

  deleteTask: async (taskId: string): Promise<ApiResponse<null>> => {
    const response = await axiosInstance.delete(API.TASK_BY_ID(taskId));
    return response.data;
  },

  setTaskCompletion: async (taskId: string, completed: boolean): Promise<ApiResponse<Task>> => {
    const response = await axiosInstance.patch(
      `${API.TASK_COMPLETION(taskId)}?completed=${completed}`
    );
    return response.data;
  },

  restoreTask: async (taskId: string): Promise<ApiResponse<Task>> => {
    const response = await axiosInstance.patch(API.TASK_RESTORE(taskId));
    return response.data;
  }
};
