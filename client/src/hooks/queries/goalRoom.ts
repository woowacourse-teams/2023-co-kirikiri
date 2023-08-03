import { CreateGoalRoomRequest, GoalRoomListRequest } from '@myTypes/goalRoom/remote';
import {
  postCreateGoalRoom,
  getGoalRoomDashboard,
  getGoalRoomList,
} from '@apis/goalRoom';
import { useSuspendedQuery } from '@hooks/queries/useSuspendedQuery';
import { useMutation } from '@tanstack/react-query';

export const useGoalRoomList = (params: GoalRoomListRequest) => {
  const { data } = useSuspendedQuery(['goalRoomList'], () => getGoalRoomList(params));
  return { goalRoomList: data };
};

export const useFetchGoalRoom = (goalRoomId: string) => {
  const { data } = useSuspendedQuery(['goalRoom', goalRoomId], () =>
    getGoalRoomDashboard(goalRoomId)
  );

  return {
    goalRoom: data,
  };
};

export const useCreateGoalRoom = () => {
  const { mutate } = useMutation(
    (body: CreateGoalRoomRequest) => postCreateGoalRoom(body),
    {
      onSuccess() {},
      onError() {},
    }
  );

  return {
    createGoalRoom: mutate,
  };
};
