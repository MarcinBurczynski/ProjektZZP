import apiClient from '../environments/axios';

export async function putCategory(
  categoryId: number,
  name: string,
  userId: number
): Promise<boolean> {
  try {
    const response = await apiClient.put('/api/categories/' + categoryId, {
      name: name,
      userId: userId,
    });
    if (response.status === 200) {
      return true;
    } else {
      console.error('Failed to put category:', response.status);
      return false;
    }
  } catch (error) {
    console.error('Error putting category:', error);
    return false;
  }
}

export async function putTask(
  taskId: number,
  title: string,
  description: string,
  status: string,
  categoryId: number,
  userId: number
): Promise<boolean> {
  try {
    const response = await apiClient.put('/api/tasks/' + taskId, {
      title: title,
      description: description,
      status: status,
      categoryId: categoryId,
      userId: userId,
    });
    if (response.status === 200) {
      return true;
    } else {
      console.error('Failed to put task:', response.status);
      return false;
    }
  } catch (error) {
    console.error('Error putting task:', error);
    return false;
  }
}

export async function putUser(
  userId: number,
  username: string,
  password: string,
  email: string,
  role: string
): Promise<boolean> {
  try {
    const response = await apiClient.put('/api/users/' + userId, {
      username: username,
      password: password,
      email: email,
      role: role,
    });
    if (response.status === 200) {
      return true;
    } else {
      console.error('Failed to put user:', response.status);
      return false;
    }
  } catch (error) {
    console.error('Error putting user:', error);
    return false;
  }
}
