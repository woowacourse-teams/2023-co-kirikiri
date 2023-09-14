const QUERY_KEYS = {
  user: {
    user: 'user',
    naver_oauth: 'naver_login',
  },
  roadmap: {
    list: 'roadmapList',
    detail: 'roadmapDetail',
    myRoadmap: 'myRoadmap',
  },
  goalRoom: {
    dashboard: 'dashboard',
    list: 'goalRoomList',
    participants: 'participants',
    certificationFeeds: 'certificationFeeds',
    my: 'myGoalRoomList',
    detail: 'goalRoomDetail',
    todos: 'goalRoomTodos',
  },
} as const;

export default QUERY_KEYS;
