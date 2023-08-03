import {
  CheckFeed,
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
  initMemberCount: number;
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

type GoalRoomLeaderType = {
  id: number;
  name: string;
};

export type GoalRoomDetailResponse = {
  goalRoomId: number;
  name: string;
  currentMemberCount: number;
  limitedMemberCount: number;
  createdAt: number[];
  startDate: number[];
  endDate: number[];
  goalRoomLeader: GoalRoomLeaderType;
};
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
