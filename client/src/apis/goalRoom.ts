import client from '@apis/axios/client';
import {
  GoalRoomBrowseResponse,
  newTodoPayload,
  CreateGoalRoomRequest,
} from '@myTypes/goalRoom/remote';

export const getGoalRoomDashboard = async (goalRoomId: string) => {
  const { data } = await client.get<GoalRoomBrowseResponse>(
    `/api/members/goal-rooms/${goalRoomId}`
  );

  return data;
};

export const postCreateGoalRoom = async (body: CreateGoalRoomRequest) => {
  const { data } = await client.post<CreateGoalRoomRequest>(`/goal-rooms`, body);

  return data;
};

export const postCreateNewTodo = (goalRoomId: string, body: newTodoPayload) => {
  return client.post(`/goal-rooms/${goalRoomId}/todos`, body);
};
