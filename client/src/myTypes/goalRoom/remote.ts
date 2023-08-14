import {
  CheckFeed,
  GoalRoomDetailType,
  GoalRoomInfoType,
  GoalRoomRecruitmentStatus,
  GoalRoomRoadmap,
  GoalRoomTodo,
} from '@myTypes/goalRoom/internal';

type FilterCondType = 'LATEST';

export type GoalRoomListRequest = {
  roadmapId: number;
  lastCreatedAt?: any;
  size?: number;
  filterCond?: FilterCondType;
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

export type GoalRoomDetailResponse = GoalRoomDetailType;

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
