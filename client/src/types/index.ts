export interface ApiResponse<T> {
  status: string;
  code: number;
  message: string;
  data: T;
  timestamp: number;
}

export interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface UserInfo {
  userName: string;
  accountName: string;
  fullName: string;
  email: string;
}

export interface Task {
  taskId: string;
  title: string;
  description: string;
  dueDate: string | null;
  createdDate: string;
  updatedDate: string;
  completedDate: string | null;
  createdBy: UserInfo;
  important: boolean;
  completed: boolean;
  deleted: boolean;
}
