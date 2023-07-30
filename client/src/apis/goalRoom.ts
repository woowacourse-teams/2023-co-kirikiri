import client from '@apis/axios/client';
import { GoalRoomBrowseResponse } from '@myTypes/goalRoom/remote';

export const browseGoalRoom = async (goalRoomId: string) => {
  const { data } = await client.get<GoalRoomBrowseResponse>(
    `/api/members/goal-rooms/${goalRoomId}`
  );

  return data;
};
