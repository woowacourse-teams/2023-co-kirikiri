import { getGoalRoomList, getGoalRoomDashboard } from '@/apis/goalRoom';
import { useSuspendedQuery } from '@hooks/queries/useSuspendedQuery';

export const useGaolRoomList = (params: any) => {
  const { data } = useSuspendedQuery(['goalRoomList'], () => getGoalRoomList(params));
  console.log(data);
  return data;
};

export const useFetchGoalRoom = (goalRoomId: string) => {
  const { data } = useSuspendedQuery(['goalRoom', goalRoomId], () =>
    getGoalRoomDashboard(goalRoomId)
  );

  return {
    goalRoom: data,
  };
};
