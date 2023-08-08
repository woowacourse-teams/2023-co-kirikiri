import {
  CreateGoalRoomRequest,
  newTodoPayload,
  GoalRoomListRequest,
  GoalRoomTodoChangeStatusRequest,
  JoinGoalRoomRequest,
} from '@myTypes/goalRoom/remote';
import {
  postCreateGoalRoom,
  getGoalRoomDashboard,
  postCreateNewTodo,
  getGoalRoomTodos,
  postCreateNewCertificationFeed,
  getGoalRoomList,
  getGoalRoomDetail,
  postToChangeTodoCheckStatus,
  postJoinGoalRoom,
} from '@apis/goalRoom';
import { useSuspendedQuery } from '@hooks/queries/useSuspendedQuery';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import useToast from '@hooks/_common/useToast';
import { useNavigate } from 'react-router-dom';

export const useGoalRoomList = (params: GoalRoomListRequest) => {
  const { data } = useSuspendedQuery(['goalRoomList', params.roadmapId], () =>
    getGoalRoomList(params)
  );
  return { goalRoomList: data };
};

export const useGoalRoomDetail = (goalRoomId: number) => {
  const { data } = useSuspendedQuery(['goalRoomDetail', goalRoomId], () =>
    getGoalRoomDetail(goalRoomId)
  );
  return { goalRoomInfo: data };
};

export const useFetchGoalRoom = (goalRoomId: string) => {
  const { data: goalRoomRes } = useSuspendedQuery(['goalRoom', goalRoomId], () =>
    getGoalRoomDashboard(goalRoomId)
  );

  return {
    goalRoom: goalRoomRes,
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

export const useCreateTodo = (goalRoomId: string) => {
  const queryClient = useQueryClient();

  const { mutate } = useMutation(
    (body: newTodoPayload) => postCreateNewTodo(goalRoomId, body),
    {
      onSuccess() {
        queryClient.invalidateQueries([
          ['goalRoom', goalRoomId],
          ['goalRoomTodos', goalRoomId],
        ]);
      },
    }
  );

  return {
    createTodo: mutate,
  };
};

export const useFetchGoalRoomTodos = (goalRoomId: string) => {
  const { data } = useSuspendedQuery(['goalRoomTodos', goalRoomId], () =>
    getGoalRoomTodos(goalRoomId)
  );

  return {
    goalRoomTodos: data,
  };
};

export const usePostChangeTodoCheckStatus = ({
  goalRoomId,
  todoId,
}: GoalRoomTodoChangeStatusRequest) => {
  const queryClient = useQueryClient();
  const { triggerToast } = useToast();

  const { mutate } = useMutation(
    () => postToChangeTodoCheckStatus({ goalRoomId, todoId }),
    {
      onSuccess() {
        triggerToast({ message: '투두리스트 상태 변경 완료!' });
        queryClient.invalidateQueries(['goalRoom', goalRoomId]);
        queryClient.invalidateQueries(['goalRoomTodos', goalRoomId]);
      },
    }
  );

  return {
    changeTodoCheckStatus: mutate,
  };
};

export const useCreateCertificationFeed = (goalRoomId: string) => {
  const queryClient = useQueryClient();

  const { mutate } = useMutation(
    (formData: FormData) => postCreateNewCertificationFeed(goalRoomId, formData),
    {
      onSuccess() {
        queryClient.invalidateQueries(['goalRoom', goalRoomId]);
      },
    }
  );

  return {
    createCertificationFeed: mutate,
  };
};

export const useJoinGoalRoom = ({ goalRoomId }: JoinGoalRoomRequest) => {
  const navigate = useNavigate();
  const { triggerToast } = useToast();
  const queryClient = useQueryClient();

  const { mutate } = useMutation(() => postJoinGoalRoom(goalRoomId), {
    onSuccess() {
      navigate(`/goalroom-dashboard/${goalRoomId}`);
      triggerToast({ message: '골룸에 참여하였습니다!' });
      queryClient.invalidateQueries(['goalRoomDetail', goalRoomId]);
    },
  });

  return {
    joinGoalRoom: mutate,
  };
};
