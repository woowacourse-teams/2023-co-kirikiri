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
} from '@apis/goalRoom';
import { useSuspendedQuery } from '@hooks/queries/useSuspendedQuery';
import { useInfiniteQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import useToast from '@hooks/_common/useToast';
import { GoalRoomRecruitmentStatus } from '@myTypes/goalRoom/internal';
import { useNavigate } from 'react-router-dom';
import QUERY_KEYS from '@constants/@queryKeys/queryKeys';

export const useGoalRoomList = (params: GoalRoomListRequest) => {
  const { roadmapId, filterCond, lastCreatedAt, size, lastId } = params;

  const { data, fetchNextPage } = useInfiniteQuery(
    ['goalRoomList', roadmapId, filterCond, lastCreatedAt, size, lastId],
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
  const { data } = useSuspendedQuery(['myGoalRoomList', statusCond], () =>
    getMyGoalRoomList(statusCond)
  );

  return { myGoalRoomList: data };
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

export const useCreateGoalRoom = (roadmapContentId: number) => {
  const queryClient = useQueryClient();

  const navigate = useNavigate();
  const { triggerToast } = useToast();
  const { mutate } = useMutation(
    (body: CreateGoalRoomRequest) => postCreateGoalRoom(body),
    {
      async onSuccess() {
        await queryClient.refetchQueries([['myGoalRoomList']]);
        navigate(`/roadmap/${roadmapContentId}/goalroom-list`);
        triggerToast({ message: '골룸을 생성했습니다!' });
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
