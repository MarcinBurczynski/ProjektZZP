import apiClient from "../environments/axios";
import { Task, User, Category } from "./interfaces";

export async function fetchCategories(): Promise<Category[]> {
  try {
    const response = await apiClient.get<Category[]>('/api/categories');
    if (response.status === 200) {
      return response.data;
    } else {
      console.error('Failed to fetch categories:', response.status);
      return [];
    }
  } catch (error) {
    console.error('Error fetching categories:', error);
    return [];
  }
}

export async function fetchTasks(): Promise<Task[]> {
  try {
    const response = await apiClient.get<Task[]>('/api/tasks');
    if (response.status === 200) {
      return response.data;
    } else {
      console.error('Failed to fetch tasks:', response.status);
      return [];
    }
  } catch (error) {
    console.error('Error fetching tasks:', error);
    return [];
  }
}

export async function fetchUsers(): Promise<User[]> {
  try {
    const response = await apiClient.get<User[]>('/api/users');
    if (response.status === 200) {
      return response.data;
    } else {
      console.error('Failed to fetch users:', response.status);
      return [];
    }
  } catch (error) {
    console.error('Error fetching users:', error);
    return [];
  }
}
