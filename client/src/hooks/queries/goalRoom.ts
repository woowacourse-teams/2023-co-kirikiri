import { CreateGoalRoomRequest } from '@myTypes/goalRoom/remote';
import {
  postCreateGoalRoom,
  getGoalRoomDashboard,
  getGoalRoomList,
} from '@apis/goalRoom';
import { useSuspendedQuery } from '@hooks/queries/useSuspendedQuery';
import { useMutation } from '@tanstack/react-query';

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
