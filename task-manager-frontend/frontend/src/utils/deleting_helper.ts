import apiClient from '../environments/axios';

export async function deleteCategory(categoryId: number): Promise<boolean> {
  try {
    const response = await apiClient.delete('/api/categories/' + categoryId);
    if (response.status === 200) {
      return true;
    } else {
      console.error('Failed to delete category:', response.status);
      return false;
    }
  } catch (error) {
    console.error('Error deleting category:', error);
    return false;
  }
}

export async function deleteTask(taskId: number): Promise<boolean> {
  try {
    const response = await apiClient.delete('/api/tasks/' + taskId);
    if (response.status === 200) {
      return true;
    } else {
      console.error('Failed to delete task:', response.status);
      return false;
    }
  } catch (error) {
    console.error('Error deleting task:', error);
    return false;
  }
}

export async function deleteUser(userId: number): Promise<boolean> {
  try {
    const response = await apiClient.delete('/api/users/' + userId);
    if (response.status === 200) {
      return true;
    } else {
      console.error('Failed to delete user:', response.status);
      return false;
    }
  } catch (error) {
    console.error('Error deleting user:', error);
    return false;
  }
}
