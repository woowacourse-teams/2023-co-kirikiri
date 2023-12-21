import { FILTER_COND } from '@constants/goalRoom/goalRoomFilter';
import {
  GoalRoomListRequest,
  GoalRoomBrowseResponse,
  CreateGoalRoomRequest,
  GoalRoomDetailResponse,
  GoalRoomTodoResponse,
  newTodoPayload,
  GoalRoomInfoResponse,
  GoalRoomTodoChangeStatusRequest,
  GoalRoomParticipantsResponse,
  ParticipantsSortOrder,
  GoalRoomCertificationFeedsResponse,
  GoalRoomNodeListResponse,
} from '@/myTypes/goalRoom/remote';
import client from '@apis/axios/client';
import { GoalRoomRecruitmentStatus, MyPageGoalRoom } from '@myTypes/goalRoom/internal';
import { API_PATH } from '@/constants/_common/api';

export const getGoalRoomList = async ({
  roadmapId,
  filterCond = FILTER_COND.latest,
  lastCreatedAt = '',
  size = 8,
  lastId,
}: GoalRoomListRequest): Promise<GoalRoomDetailResponse> => {
  const { data } = await client.get<GoalRoomDetailResponse>(
    API_PATH.GOALROOMS(roadmapId),
    {
      params: {
        ...(lastId && { lastId }),
        filterCond,
        lastCreatedAt,
        size,
      },
    }
  );

  return data;
};

export const getMyGoalRoomList = async (statusCond: GoalRoomRecruitmentStatus) => {
  const { data } = await client.get<MyPageGoalRoom[]>(API_PATH.MY_GOALROOMS(statusCond));

  return data;
};

export const getGoalRoomDetail = async (
  goalRoomId: number
): Promise<GoalRoomInfoResponse> => {
  const { data } = await client.get<GoalRoomInfoResponse>(
    API_PATH.GOALROOM_DETAIL(goalRoomId)
  );
  return data;
};

export const getGoalRoomDashboard = async (goalRoomId: string) => {
  const { data } = await client.get<GoalRoomBrowseResponse>(
    API_PATH.GOALROOM_DASHBOARD(goalRoomId)
  );
  return data;
};

export const postCreateGoalRoom = async (body: CreateGoalRoomRequest) => {
  const { data } = await client.post(API_PATH.CREATE_GOALROOM, body);

  return data;
};

export const getGoalRoomTodos = async (goalRoomId: string) => {
  const { data } = await client.get<GoalRoomTodoResponse>(
    API_PATH.GOALROOM_TODOS(goalRoomId)
  );

  return data;
};

export const postToChangeTodoCheckStatus = async ({
  goalRoomId,
  todoId,
}: GoalRoomTodoChangeStatusRequest) => {
  return client.post(API_PATH.CHANGE_TODO_CHECKS(goalRoomId, todoId));
};

export const postCreateNewTodo = (goalRoomId: string, body: newTodoPayload) => {
  return client.post(API_PATH.CREATE_TODO(goalRoomId), body);
};

export const postCreateNewCertificationFeed = (
  goalRoomId: string,
  formData: FormData
) => {
  return client.post(API_PATH.CREATE_FEED(goalRoomId), formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};

export const postJoinGoalRoom = (goalRoomId: string) => {
  return client.post(API_PATH.JOIN_GOALROOM(goalRoomId));
};

export const getGoalRoomParticipants = async (
  goalRoomId: string,
  participantsSortOrder: ParticipantsSortOrder
) => {
  const { data } = await client.get<GoalRoomParticipantsResponse>(
    API_PATH.GOALROOM_PARTICIPANTS(goalRoomId),
    {
      params: {
        sortCond: participantsSortOrder,
      },
    }
  );

  return data;
};

export const getCertificationFeeds = async (goalRoomId: string) => {
  const { data } = await client.get<GoalRoomCertificationFeedsResponse>(
    API_PATH.GOALROOM_FEEDS(goalRoomId)
  );

  return data;
};

export const postStartGoalRoom = async (goalRoomId: string) => {
  return client.post(API_PATH.START_GOALROOM(goalRoomId));
};

export const getGoalRoomNodeList = async (goalRoomId: string) => {
  const { data } = await client.get<GoalRoomNodeListResponse>(
    API_PATH.GOALROOM_NODE_LIST(goalRoomId)
  );
  return data;
};
