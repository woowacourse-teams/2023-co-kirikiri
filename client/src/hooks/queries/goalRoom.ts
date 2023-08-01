import { getGoalRoomList } from '@/apis/goalRoom';
import { useSuspendedQuery } from './useSuspendedQuery';

export const useGaolRoomList = (params: any) => {
  const { data } = useSuspendedQuery(['goalRoomList'], () => getGoalRoomList(params));
  console.log(data);
  return data;
};
