export type GoalRoomRecruitmentStatus =
  | 'RECRUITING'
  | 'RUNNING'
  | 'COMPLETED'
  | 'RECRUIT_COMPLETED';

export type GoalRoomRoadmap = {
  hasFrontNode: boolean;
  hasBackNode: boolean;
  nodes: GoalRoomNode[];
};

export type GoalRoomNode = {
  title: string;
  startDate: string;
  endDate: string;
  checkCount: number;
};

export type GoalRoomTodo = {
  content: string;
  startDate: string;
  endDate: string;
};

export type CheckFeed = {
  id: number;
  imageUrl: string;
};
