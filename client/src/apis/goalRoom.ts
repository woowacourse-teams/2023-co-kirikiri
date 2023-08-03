import {
  RoadmapListRequest,
  GoalRoomBrowseResponse,
  CreateGoalRoomRequest,
} from '@/myTypes/goalRoom/remote';
import client from '@apis/axios/client';

export const getGoalRoomList = async ({
  lastValue = null,
  size = 10,
  filterCond = 'LATEST',
}: RoadmapListRequest): Promise<any> => {
  const { data } = await client.get(
    `/goal-rooms?lastValue=${lastValue}&size=${size}&filterCond=${filterCond}`
  );
  return data;
};

export const getGoalRoomDashboard = async (goalRoomId: string) => {
  const { data } = await client.get<GoalRoomBrowseResponse>(
    `/api/members/goal-rooms/${goalRoomId}`
  );
};

export const postCreateGoalRoom = async (body: CreateGoalRoomRequest) => {
  const { data } = await client.post<CreateGoalRoomRequest>(`/goal-rooms`, body);

  return data;
};
