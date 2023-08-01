import { RoadmapListRequest } from '@/myTypes/goalRoom/remote';
import client from './axios/client';

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
