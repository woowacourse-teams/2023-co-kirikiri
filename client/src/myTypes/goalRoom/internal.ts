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
  id: number;
  title: string;
  startDate: string;
  endDate: string;
  checkCount: number;
};

export type GoalRoomTodo = {
  id: number;
  content: string;
  startDate: string;
  endDate: string;
};

export type CheckFeed = {
  id: number;
  imageUrl: string;
};

type GoalRoomLeaderType = {
  id: number;
  name: string;
};

export type GoalRoomDetailType = {
  goalRoomId: number;
  name: string;
  currentMemberCount: number;
  limitedMemberCount: number;
  createdAt: number[];
  startDate: number[];
  endDate: number[];
  goalRoomLeader: GoalRoomLeaderType;
};

type GoalRoomNodesType = {
  title: string;
  startDate: number[];
  endDate: number[];
  checkCount: number;
};

export type GoalRoomInfoType = {
  name: string;
  currentMemberCount: number;
  limitedMemberCount: number;
  goalRoomNodes: GoalRoomNodesType[];
  period: number;
  isJoined: boolean;
};
