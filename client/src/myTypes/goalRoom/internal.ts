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
  check: {
    isChecked: boolean;
  };
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
  period: number;
  isJoined: boolean;
  goalRoomLeader: GoalRoomLeaderType;
  status: GoalRoomRecruitmentStatus;
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

export type MyPageGoalRoom = {
  goalRoomId: number;
  name: string;
  goalRoomStatus: GoalRoomRecruitmentStatus;
  currentMemberCount: number;
  limitedMemberCount: number;
  createdAt: string;
  startDate: string;
  endDate: string;
  goalRoomLeader: GoalRoomLeaderType;
};

export type GoalRoomNodeType = {
  id: number;
  title: string;
  startData: string[];
  endDate: string[];
  checkCount: number;
  description: string;
  imageUrls: string[];
};

export type GoalRoomNodeListType = GoalRoomNodeType[];
