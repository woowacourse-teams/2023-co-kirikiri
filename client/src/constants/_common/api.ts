import { GoalRoomRecruitmentStatus } from '@/myTypes/goalRoom/internal';

export const API_PATH = {
  GOALROOMS: (roadmapId: number) => `/roadmaps/${roadmapId}/goal-rooms`,
  MY_GOALROOMS: (statusCond: GoalRoomRecruitmentStatus) =>
    `/goal-rooms/me?statusCond=${statusCond}`,
  GOALROOM_DETAIL: (goalRoomId: number) => `/goal-rooms/${goalRoomId}`,
  GOALROOM_DASHBOARD: (goalRoomId: string) => `/goal-rooms/${goalRoomId}/me`,
  CREATE_GOALROOM: `/goal-rooms`,
  GOALROOM_TODOS: (goalRoomId: string) => `/goal-rooms/${goalRoomId}/todos`,
  CHANGE_TODO_CHECKS: (goalRoomId: string, todoId: string) =>
    `/goal-rooms/${goalRoomId}/todos/${todoId}`,
  CREATE_TODO: (goalRoomId: string) => `/goal-rooms/${goalRoomId}/todos`,
  CREATE_FEED: (goalRoomId: string) => `/goal-rooms/${goalRoomId}/checkFeeds`,
  JOIN_GOALROOM: (goalRoomId: string) => `/goal-rooms/${goalRoomId}/join`,
  GOALROOM_PARTICIPANTS: (goalRoomId: string) => `/goal-rooms/${goalRoomId}/members`,
  GOALROOM_FEEDS: (goalRoomId: string) => `/goal-rooms/${goalRoomId}/checkFeeds`,
  START_GOALROOM: (goalRoomId: string) => `/goal-rooms/${goalRoomId}/start`,
  GOALROOM_NODE_LIST: (goalRoomId: string) => `/goal-rooms/${goalRoomId}/nodes`,

  ROADMAPS: '/roadmaps',
  ROADMAP_SEARCH: '/roadmaps/search',
  ROADMAP_DETAIL: (roadmapId: number) => `/roadmaps/${roadmapId}`,
  CREATE_ROADMAP: '/roadmaps',
  MY_ROADMAPS: '/roadmaps/me',

  SIGN_UP: '/members/join',
  NAVER_LOGIN_REDIRECT: '/auth/oauth/naver',
  NAVER_TOKEN: '/auth/login/oauth',
  LOGIN: '/auth/login',
  USER_INFO: '/members/me',
} as const;
