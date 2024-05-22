import {
  CreateGoalRoomRequest,
  newTodoPayload,
  GoalRoomListRequest,
  GoalRoomTodoChangeStatusRequest,
  JoinGoalRoomRequest,
  ParticipantsSortOrder,
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
  getMyGoalRoomList,
  getGoalRoomParticipants,
  getCertificationFeeds,
  postStartGoalRoom,
  getGoalRoomNodeList,
} from '@apis/goalRoom';
import { useSuspendedQuery } from '@hooks/queries/useSuspendedQuery';
import { useInfiniteQuery, useQueryClient } from '@tanstack/react-query';
import { GoalRoomRecruitmentStatus } from '@myTypes/goalRoom/internal';
import { useNavigate } from 'react-router-dom';
import QUERY_KEYS from '@constants/@queryKeys/queryKeys';
import { useMutationWithKey } from './useMutationWithKey';

export const useGoalRoomList = (params: GoalRoomListRequest) => {
  const { roadmapId } = params;

  const { data, fetchNextPage } = useInfiniteQuery(
    [QUERY_KEYS.goalRoom.list, roadmapId],
    ({ pageParam }) => getGoalRoomList({ ...params, lastId: pageParam }),
    {
      getNextPageParam: (lastPage) =>
        lastPage.hasNext
          ? lastPage.responses[lastPage.responses.length - 1]?.goalRoomId
          : undefined,
    }
  );

  const responses = data?.pages.flatMap((page) => page.responses) || [];

  const hasNext = Boolean(data?.pages[data.pages.length - 1]?.hasNext);

  return { goalRoomListResponse: { responses, hasNext }, fetchNextPage };
};

export const useMyPageGoalRoomList = (statusCond: GoalRoomRecruitmentStatus) => {
  const { data } = useSuspendedQuery([QUERY_KEYS.goalRoom.my, statusCond], () =>
    getMyGoalRoomList(statusCond)
  );

  return { myGoalRoomList: data };
};

export const useGoalRoomDetail = (goalRoomId: number) => {
  const { data } = useSuspendedQuery([QUERY_KEYS.goalRoom.detail, goalRoomId], () =>
    getGoalRoomDetail(goalRoomId)
  );
  return { goalRoomInfo: data };
};

export const useFetchGoalRoom = (goalRoomId: string) => {
  const { data } = useSuspendedQuery([QUERY_KEYS.goalRoom.dashboard, goalRoomId], () =>
    getGoalRoomDashboard(goalRoomId)
  );

  return {
    goalRoom: data,
  };
};

export const useCreateGoalRoom = (roadmapId: number) => {
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  const { mutate } = useMutationWithKey(
    'CREATE_GOALROOM',
    (body: CreateGoalRoomRequest) => postCreateGoalRoom(body),
    {
      onSuccess: async () => {
        navigate(`/roadmap/${roadmapId}/goalroom-list`);
        await queryClient.refetchQueries([QUERY_KEYS.goalRoom.list, roadmapId]);
        await queryClient.refetchQueries([QUERY_KEYS.goalRoom.my, roadmapId]);
      },
    }
  );

  return {
    createGoalRoom: mutate,
  };
};

export const useCreateTodo = (goalRoomId: string) => {
  const queryClient = useQueryClient();

  const { mutate } = useMutationWithKey(
    'CREATE_TODO',
    (body: newTodoPayload) => postCreateNewTodo(goalRoomId, body),
    {
      onSuccess() {
        queryClient.invalidateQueries([QUERY_KEYS.goalRoom.dashboard, goalRoomId]);
        queryClient.invalidateQueries([QUERY_KEYS.goalRoom.todos, goalRoomId]);
      },
    }
  );

  return {
    createTodo: mutate,
  };
};

export const useFetchGoalRoomTodos = (goalRoomId: string) => {
  const { data } = useSuspendedQuery([QUERY_KEYS.goalRoom.todos, goalRoomId], () =>
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

  const { mutate } = useMutationWithKey(
    'CHECK_TODO',
    () => postToChangeTodoCheckStatus({ goalRoomId, todoId }),
    {
      onSuccess() {
        queryClient.invalidateQueries([QUERY_KEYS.goalRoom.dashboard, goalRoomId]);
        queryClient.invalidateQueries([QUERY_KEYS.goalRoom.todos, goalRoomId]);
      },
    }
  );

  return {
    changeTodoCheckStatus: mutate,
  };
};

export const useCreateCertificationFeed = (
  goalRoomId: string,
  onSuccessCallbackFunc: () => void
) => {
  const queryClient = useQueryClient();

  const { mutate } = useMutationWithKey(
    'CREATE_FEED',
    (formData: FormData) => postCreateNewCertificationFeed(goalRoomId, formData),
    {
      onSuccess() {
        queryClient.invalidateQueries([QUERY_KEYS.goalRoom.dashboard, goalRoomId]);
        queryClient.invalidateQueries([
          QUERY_KEYS.goalRoom.certificationFeeds,
          goalRoomId,
        ]);
        onSuccessCallbackFunc();
      },
    }
  );

  return {
    createCertificationFeed: mutate,
  };
};

export const useJoinGoalRoom = ({ goalRoomId }: JoinGoalRoomRequest) => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const { mutate } = useMutationWithKey(
    'JOIN_GOALROOM',
    () => postJoinGoalRoom(goalRoomId),
    {
      onSuccess() {
        navigate(`/goalroom-dashboard/${goalRoomId}`);
        queryClient.invalidateQueries([QUERY_KEYS.goalRoom.detail, goalRoomId]);
      },
    }
  );

  return {
    joinGoalRoom: mutate,
  };
};

export const useFetchGoalRoomParticipants = (
  goalRoomId: string,
  participantsSortOrder: ParticipantsSortOrder
) => {
  const { data } = useSuspendedQuery([QUERY_KEYS.goalRoom.participants, goalRoomId], () =>
    getGoalRoomParticipants(goalRoomId, participantsSortOrder)
  );

  return {
    goalRoomParticipants: data,
  };
};

export const useCertificationFeeds = (goalRoomId: string) => {
  const { data } = useSuspendedQuery(
    [QUERY_KEYS.goalRoom.certificationFeeds, goalRoomId],
    () => getCertificationFeeds(goalRoomId)
  );

  return {
    certificationFeeds: data,
  };
};

export const useStartGoalRoom = (goalRoomId: string) => {
  const queryClient = useQueryClient();

  const { mutate } = useMutationWithKey(
    'START_GOALROOM',
    () => postStartGoalRoom(goalRoomId),
    {
      onSuccess() {
        queryClient.invalidateQueries([QUERY_KEYS.goalRoom.dashboard, goalRoomId]);
      },
    }
  );

  return {
    startGoalRoom: mutate,
  };
};

export const useGoalRoomNodeList = (goalRoomId: string) => {
  const { data } = useSuspendedQuery(['goalRoomNodeList', goalRoomId], () =>
    getGoalRoomNodeList(goalRoomId)
  );

  return {
    goalRoomNodeList: data,
  };
};
