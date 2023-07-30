export default {
  data: {
    name: '골룸',
    status: '모집중',
    currentMemberCount: 10,
    initMemberCount: 15,
    period: 17,
    roadmapContentId: 1,
    goalRoomRoadmap: {
      hasFrontNode: true,
      hasBackNode: true,
      nodes: [
        {
          title: '로드맵 1주차',
          startDate: [2023, 7, 19],
          endDate: [2023, 7, 30],
          checkCount: 10,
        },
        {
          title: '로드맵 2주차',
          startDate: [2023, 8, 1],
          endDate: [2023, 8, 5],
          checkCount: 2,
        },
      ],
    },
    goalRoomTodos: [
      {
        id: 1,
        content: '투두 내용',
        startDate: [2023, 7, 19],
        endDate: [2023, 7, 20],
      },
      {
        id: 2,
        content: '투두 내용 2',
        startDate: [2023, 7, 21],
        endDate: [2023, 7, 24],
      },
    ],
    cheekFeeds: [
      {
        id: 1,
        imageUrl: '이미지-경로',
      },
      {
        id: 2,
        imageUrl: '이미지-경로',
      },
    ],
  },

  getGoalRoomBrowseResponse() {
    return this.data;
  },
};
