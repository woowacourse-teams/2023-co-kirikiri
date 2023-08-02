import {
  CheckFeed,
  GoalRoomRecruitmentStatus,
  GoalRoomRoadmap,
  GoalRoomTodo,
} from '@myTypes/goalRoom/internal';

export type GoalRoomBrowseResponse = {
  name: string;
  status: GoalRoomRecruitmentStatus;
  currentMemberCount: number;
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
  goalRoomTodo: Omit<GoalRoomTodo, 'id'>;
  goalRoomRoadmapNodeRequests: GoalRoomRoadmapNodeRequestsType[];
};

export type newTodoPayload = {
  goalRoomId: string;
  content: string;
  startDate: string;
  endDate: string;
};
