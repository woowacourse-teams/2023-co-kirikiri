import {
  GoalRoomListRequest,
  GoalRoomBrowseResponse,
  CreateGoalRoomRequest,
  GoalRoomDetailResponse,
  GoalRoomTodoResponse,
  newTodoPayload,
  GoalRoomInfoResponse,
  GoalRoomTodoChangeStatusRequest,
  GoalRoomParticipantsResponse,
  ParticipantsSortOrder,
  GoalRoomCertificationFeedsResponse,
} from '@/myTypes/goalRoom/remote';
import client from '@apis/axios/client';
import { GoalRoomRecruitmentStatus, MyPageGoalRoom } from '@myTypes/goalRoom/internal';

export const getGoalRoomList = async ({
  roadmapId,
  filterCond = 'LATEST',
  lastCreatedAt = '',
  size = 10,
}: GoalRoomListRequest): Promise<GoalRoomDetailResponse> => {
  const { data } = await client.get<GoalRoomDetailResponse>(
    `/roadmaps/${roadmapId}/goal-rooms?filterCond=${filterCond}&lastCreatedAt=${lastCreatedAt}&size=${size}`
  );
  return data;
};

export const getMyGoalRoomList = async (statusCond: GoalRoomRecruitmentStatus) => {
  const { data } = await client.get<MyPageGoalRoom[]>(
    `/goal-rooms/me?statusCond=${statusCond}`
  );

  return data;
};

export const getGoalRoomDetail = async (
  goalRoomId: number
): Promise<GoalRoomInfoResponse> => {
  const { data } = await client.get<GoalRoomInfoResponse>(`/goal-rooms/${goalRoomId}`);
  return data;
};

export const getGoalRoomDashboard = async (goalRoomId: string) => {
  const { data } = await client.get<GoalRoomBrowseResponse>(
    `/goal-rooms/${goalRoomId}/me`
  );
  return data;
};

export const postCreateGoalRoom = async (body: CreateGoalRoomRequest) => {
  const { data } = await client.post(`/goal-rooms`, body);

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

export const postJoinGoalRoom = (goalRoomId: string) => {
  return client.post(`/goal-rooms/${goalRoomId}/join`);
};

export const getGoalRoomParticipants = async (
  goalRoomId: string,
  participantsSortOrder: ParticipantsSortOrder
) => {
  const { data } = await client.get<GoalRoomParticipantsResponse>(
    `/goal-rooms/${goalRoomId}/members`,
    {
      params: {
        sortCond: participantsSortOrder,
      },
    }
  );

  return data;
};

export const getCertificationFeeds = async (goalRoomId: string) => {
  const { data } = await client.get<GoalRoomCertificationFeedsResponse>(
    `/goal-rooms/${goalRoomId}/checkFeeds`
  );

  return data;
};

export const postStartGoalRoom = async (goalRoomId: string) => {
  return client.post(`/goal-rooms/${goalRoomId}/start`);
};
