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
import { useInfiniteQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import useToast from '@hooks/_common/useToast';
import { GoalRoomRecruitmentStatus } from '@myTypes/goalRoom/internal';
import { useNavigate } from 'react-router-dom';
import QUERY_KEYS from '@constants/@queryKeys/queryKeys';

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
  const { data: goalRoomResponse } = useSuspendedQuery(
    [QUERY_KEYS.goalRoom.dashboard, goalRoomId],
    () => getGoalRoomDashboard(goalRoomId)
  );

  return {
    goalRoom: goalRoomResponse,
  };
};

export const useCreateGoalRoom = (roadmapContentId: number) => {
  const queryClient = useQueryClient();

  const navigate = useNavigate();
  const { triggerToast } = useToast();
  const { mutate } = useMutation(
    (body: CreateGoalRoomRequest) => postCreateGoalRoom(body),
    {
      async onSuccess() {
        await queryClient.refetchQueries([QUERY_KEYS.goalRoom.list, roadmapContentId]);
        await queryClient.refetchQueries([QUERY_KEYS.goalRoom.my, roadmapContentId]);
        navigate(`/roadmap/${roadmapContentId}/goalroom-list`);
        triggerToast({ message: '모임을 생성했습니다!' });
      },
      onError() {},
    }
  );

  return {
    createGoalRoom: mutate,
  };
};

export const useCreateTodo = (goalRoomId: string) => {
  const queryClient = useQueryClient();
  const { triggerToast } = useToast();

  const { mutate } = useMutation(
    (body: newTodoPayload) => postCreateNewTodo(goalRoomId, body),
    {
      onSuccess() {
        queryClient.invalidateQueries([QUERY_KEYS.goalRoom.dashboard, goalRoomId]);
        queryClient.invalidateQueries([QUERY_KEYS.goalRoom.todos, goalRoomId]);

        triggerToast({ message: '새로운 투두리스트가 등록되었습니다.' });
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
  const { triggerToast } = useToast();

  const { mutate } = useMutation(
    () => postToChangeTodoCheckStatus({ goalRoomId, todoId }),
    {
      onSuccess() {
        triggerToast({ message: '투두리스트 상태 변경 완료!' });
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
  const { triggerToast } = useToast();
  const queryClient = useQueryClient();

  const { mutate } = useMutation(
    (formData: FormData) => postCreateNewCertificationFeed(goalRoomId, formData),
    {
      onSuccess() {
        queryClient.invalidateQueries([QUERY_KEYS.goalRoom.dashboard, goalRoomId]);
        triggerToast({ message: '인증 피드가 등록되었습니다' });
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
  const { triggerToast } = useToast();
  const queryClient = useQueryClient();

  const { mutate } = useMutation(() => postJoinGoalRoom(goalRoomId), {
    onSuccess() {
      navigate(`/goalroom-dashboard/${goalRoomId}`);
      triggerToast({ message: '모임에 참여하였습니다!' });
      queryClient.invalidateQueries([QUERY_KEYS.goalRoom.detail, goalRoomId]);
    },
  });

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
  const { triggerToast } = useToast();
  const queryClient = useQueryClient();

  const { mutate } = useMutation(() => postStartGoalRoom(goalRoomId), {
    onSuccess() {
      triggerToast({ message: '모임이 시작되었습니다' });
      queryClient.invalidateQueries([QUERY_KEYS.goalRoom.dashboard, goalRoomId]);
    },
  });

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
