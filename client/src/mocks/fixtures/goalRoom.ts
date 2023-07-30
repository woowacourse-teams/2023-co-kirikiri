import { GoalRoomBrowseResponse } from '@myTypes/goalRoom/remote';

type GoalRoomFixture = {
  data: GoalRoomBrowseResponse;
  getBrowsedGoalRoom: () => GoalRoomBrowseResponse;
};

const fixture: GoalRoomFixture = {
  data: {
    name: '골룸',
    status: 'RECRUITING',
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
          startDate: '2023-07-19',
          endDate: '2023-07-30',
          checkCount: 10,
        },
        {
          title: '로드맵 2주차',
          startDate: '2023-08-01',
          endDate: '2023-08-05',
          checkCount: 2,
        },
      ],
    },
    goalRoomTodos: [
      {
        id: 1,
        content: '투두 내용',
        startDate: '2023-07-19',
        endDate: '2023-07-20',
      },
      {
        id: 2,
        content: '투두 내용 2',
        startDate: '2023-07-21',
        endDate: '2023-07-24',
      },
    ],
    checkFeeds: [
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

  getBrowsedGoalRoom() {
    return this.data;
  },
};

export default fixture;
