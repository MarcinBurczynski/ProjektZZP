import apiClient from '../environments/axios';

export async function postCategory(
  name: string,
  userId: number
): Promise<boolean> {
  try {
    const response = await apiClient.post('/api/categories', {
      name: name,
      userId: userId,
    });
    if (response.status === 200) {
      return true;
    } else {
      console.error('Failed to post category:', response.status);
      return false;
    }
  } catch (error) {
    console.error('Error posting category:', error);
    return false;
  }
}

export async function postTask(
  title: string,
  description: string,
  status: string,
  categoryId: number,
  userId: number
): Promise<boolean> {
  try {
    const response = await apiClient.post('/api/tasks', {
      title: title,
      description: description,
      status: status,
      categoryId: categoryId,
      userId: userId,
    });
    if (response.status === 200) {
      return true;
    } else {
      console.error('Failed to post task:', response.status);
      return false;
    }
  } catch (error) {
    console.error('Error posting task:', error);
    return false;
  }
}

export async function postUser(
  username: string,
  password: string,
  email: string,
  role: string
): Promise<boolean> {
  try {
    const response = await apiClient.post('/api/users', {
      username: username,
      password: password,
      email: email,
      role: role,
    });
    if (response.status === 200) {
      return true;
    } else {
      console.error('Failed to fetch users:', response.status);
      return false;
    }
  } catch (error) {
    console.error('Error fetching users:', error);
    return false;
  }
}
