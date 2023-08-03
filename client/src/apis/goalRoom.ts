import client from '@apis/axios/client';
import {
  GoalRoomBrowseResponse,
  newTodoPayload,
  CreateGoalRoomRequest,
  GoalRoomTodoResponse,
} from '@myTypes/goalRoom/remote';

export const getGoalRoomDashboard = async (goalRoomId: string) => {
  const { data } = await client.get<GoalRoomBrowseResponse>(
    `/goal-rooms/${goalRoomId}/me`
  );

  return data;
};

export const postCreateGoalRoom = async (body: CreateGoalRoomRequest) => {
  const { data } = await client.post<CreateGoalRoomRequest>(`/goal-rooms`, body);

  return data;
};

export const getGoalRoomTodos = async (goalRoomId: string) => {
  const { data } = await client.get<GoalRoomTodoResponse>(
    `/goal-rooms/${goalRoomId}/todos`
  );

  return data;
};

export const postCreateNewTodo = (goalRoomId: string, body: newTodoPayload) => {
  return client.post(`/goal-rooms/${goalRoomId}/todos`, body);
};

export const postCreateNewCertificationFeed = (
  goalRoomId: string,
  formData: FormData
) => {
  return client.post(`/goal-rooms/${goalRoomId}/checkFeeds`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};
