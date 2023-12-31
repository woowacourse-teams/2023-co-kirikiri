import { FILTER_COND } from '@constants/goalRoom/goalRoomFilter';
import {
  CheckFeed,
  GoalRoomDetailType,
  GoalRoomInfoType,
  GoalRoomNodeListType,
  GoalRoomRecruitmentStatus,
  GoalRoomRoadmap,
  GoalRoomTodo,
} from '@myTypes/goalRoom/internal';

type FilterCondType = (typeof FILTER_COND)[keyof typeof FILTER_COND];

export type GoalRoomListRequest = {
  roadmapId: number;
  lastCreatedAt?: any;
  size?: number;
  filterCond?: FilterCondType;
  lastId?: number | null;
};

export type GoalRoomBrowseResponse = {
  name: string;
  status: GoalRoomRecruitmentStatus;
  currentMemberCount: number;
  leaderId: number;
  limitedMemberCount: number;
  startDate: string;
  endDate: string;
  roadmapContentId: number;
  goalRoomRoadmap: GoalRoomRoadmap;
  goalRoomTodos: GoalRoomTodo[];
  checkFeeds: CheckFeed[];
};

export type GoalRoomRoadmapNodeRequestsType = {
  roadmapNodeId: number;
  checkCount: number;
  startDate: string;
  endDate: string;
};

export type CreateGoalRoomRequest = {
  roadmapContentId: number;
  name: string;
  limitedMemberCount: number;
  goalRoomTodo: Partial<GoalRoomTodo>;
  goalRoomRoadmapNodeRequests: GoalRoomRoadmapNodeRequestsType[];
};

export type GoalRoomDetailResponse = {
  responses: GoalRoomDetailType[];
  hasNext: boolean;
};

export type GoalRoomInfoResponse = GoalRoomInfoType;

export type newTodoPayload = {
  content: string;
  startDate: string;
  endDate: string;
};

export type GoalRoomTodoResponse = GoalRoomTodo[];

export type GoalRoomTodoChangeStatusRequest = {
  goalRoomId: string;
  todoId: string;
};

export type JoinGoalRoomRequest = {
  goalRoomId: string;
};

export type GoalRoomParticipant = {
  memberId: number;
  nickname: string;
  imagePath: string;
  accomplishmentRate: number;
};

export type GoalRoomParticipantsResponse = GoalRoomParticipant[];

export type ParticipantsSortOrder = 'ACCOMPLISHMENT_RATE' | 'JOINED_ASC' | 'JOINED_DESC';

export type UserType = {
  id: number;
  nickname: string;
  imageUrl: string;
};

export type CheckFeedType = {
  id: number;
  imageUrl: string;
  description: string;
  createdAt: string;
};

export type CertificationFeedType = {
  member: UserType;
  checkFeed: CheckFeedType;
};

export type GoalRoomCertificationFeedsResponse = CertificationFeedType[];

export type GoalRoomNodeListResponse = GoalRoomNodeListType;
