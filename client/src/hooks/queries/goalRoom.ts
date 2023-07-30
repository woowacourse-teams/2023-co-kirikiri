import { browseGoalRoom } from '@apis/goalRoom';
import { useSuspendedQuery } from '@hooks/queries/useSuspendedQuery';

export const useFetchGoalRoom = (goalRoomId: string) => {
  const { data } = useSuspendedQuery(['goalRoom', goalRoomId], () =>
    browseGoalRoom(goalRoomId)
  );

  return {
    goalRoom: data,
  };
};
