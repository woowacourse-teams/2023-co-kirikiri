const QUERY_KEYS = {
  user: {
    user: 'user',
  },
  roadmap: {
    list: 'roadmapList',
    detail: 'roadmapDetail',
  },
  goalRoom: {
    list: 'goalRoomList',
    participants: 'participants',
    certificationFeeds: 'certificationFeeds',
    my: 'myGoalRoomList',
    detail: 'goalRoomDetail',
    dashboard: 'goalRoom',
    todos: 'goalRoomTodos',
  },
} as const;

export default QUERY_KEYS;
