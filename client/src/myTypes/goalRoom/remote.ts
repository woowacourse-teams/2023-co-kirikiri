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