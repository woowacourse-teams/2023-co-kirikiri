const QUERY_KEYS = {
  user: {
    user: 'user',
  },
  roadmap: {
    list: 'roadmapList',
    detail: 'roadmapDetail',
    myRoadmap: 'myRoadmap',
  },
  goalRoom: {
    participants: 'participants',
    certificationFeeds: 'certificationFeeds',
  },
} as const;

export default QUERY_KEYS;
