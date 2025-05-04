export interface User {
  id: number;
  username: string;
  password: string;
  email: string;
  role: string;
}

export interface Category {
  id: number;
  name: string;
  userId: number;
}

export interface Task {
  id: number;
  title: string;
  description: string;
  status: string;
  categoryId: number;
  userId: number;
}
