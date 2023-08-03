import {
  GoalRoomListRequest,
  GoalRoomBrowseResponse,
  CreateGoalRoomRequest,
  GoalRoomDetailResponse,
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
    `/api/members/goal-rooms/${goalRoomId}`
  );
  return data;
};

export const postCreateGoalRoom = async (body: CreateGoalRoomRequest) => {
  const { data } = await client.post<CreateGoalRoomRequest>(`/goal-rooms`, body);

  return data;
};
