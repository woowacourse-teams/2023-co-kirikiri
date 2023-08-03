import {
  GoalRoomListRequest,
  GoalRoomBrowseResponse,
  CreateGoalRoomRequest,
  GoalRoomDetailResponse,
  GoalRoomTodoResponse,
  newTodoPayload,
  GoalRoomTodoChangeStatusRequest,
} from '@/myTypes/goalRoom/remote';
import client from '@apis/axios/client';

export const getGoalRoomList = async ({
  roadmapId,
  filterCond = 'LATEST',
  lastCreatedAt = '',
  size = 10,
}: GoalRoomListRequest): Promise<GoalRoomDetailResponse[]> => {
  const { data } = await client.get<GoalRoomDetailResponse[]>(
    `/roadmaps/${roadmapId}/goal-rooms?filterCond=${filterCond}&lastCreatedAt=${lastCreatedAt}&size=${size}`
  );
  return data;
};

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

export const postToChangeTodoCheckStatus = async ({
  goalRoomId,
  todoId,
}: GoalRoomTodoChangeStatusRequest) => {
  return client.post(`/goal-rooms/${goalRoomId}/todos/${todoId}`);
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
