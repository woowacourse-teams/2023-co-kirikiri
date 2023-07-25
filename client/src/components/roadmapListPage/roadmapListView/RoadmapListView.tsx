import type { RoadmapListResponseType } from '@myTypes/roadmap';
import RoadmapItem from '@components/_common/roadmapItem/RoadmapItem';
import Categories from '../categories/Categories';

import * as S from './RoadmapListView.styles';

const DummyData: RoadmapListResponseType = {
  currentPage: 3,
  totalPage: 8,
  data: [
    {
      roadmapId: 1,
      roadmapTitle: '안녕하세요 로드맵입니다',
      introduction: '로드맵 소개글입니다',
      difficulty: 'VERY_DIFFICULT',
      recommendedRoadmapPeriod: 10,
      creator: {
        id: 1,
        name: 'woody',
      },
      category: {
        id: 1,
        name: '어학',
        iconName: 'LanguageIcon',
      },
    },
    {
      roadmapId: 2,
      roadmapTitle: '안녕하세요 로드맵입니다',
      introduction: '로드맵 소개글입니다',
      difficulty: 'EASY',
      recommendedRoadmapPeriod: 10,
      creator: {
        id: 2,
        name: 'nave',
      },
      category: {
        id: 2,
        name: 'IT',
        iconName: 'ITIcon',
      },
    },
    {
      roadmapId: 3,
      roadmapTitle: '안녕하세요 로드맵입니다',
      introduction: '로드맵 소개글입니다',
      difficulty: 'VERY_EASY',
      recommendedRoadmapPeriod: 10,
      creator: {
        id: 3,
        name: 'sunshot',
      },
      category: {
        id: 3,
        name: '시험',
        iconName: 'ExamIcon',
      },
    },
    {
      roadmapId: 4,
      roadmapTitle: '안녕하세요 로드맵입니다',
      introduction: '로드맵 소개글입니다',
      difficulty: 'DIFFICULT',
      recommendedRoadmapPeriod: 10,
      creator: {
        id: 1,
        name: 'woody',
      },
      category: {
        id: 4,
        name: '운동',
        iconName: 'ExerciseIcon',
      },
    },
    {
      roadmapId: 5,
      roadmapTitle: '안녕하세요 로드맵입니다',
      introduction: '로드맵 소개글입니다',
      difficulty: 'NORMAL',
      recommendedRoadmapPeriod: 10,
      creator: {
        id: 2,
        name: 'nave',
      },
      category: {
        id: 5,
        name: '게임',
        iconName: 'GameIcon',
      },
    },
    {
      roadmapId: 6,
      roadmapTitle: '안녕하세요 로드맵입니다',
      introduction: '로드맵 소개글입니다',
      difficulty: 'EASY',
      recommendedRoadmapPeriod: 10,
      creator: {
        id: 3,
        name: 'sunshot',
      },
      category: {
        id: 6,
        name: '음악',
        iconName: 'MusicIcon',
      },
    },
  ],
};

const RoadmapListView = () => {
  return (
    <S.RoadmapListView>
      <Categories />
      <S.RoadmapList>
        {DummyData.data.map((item) => (
          <RoadmapItem key={item.roadmapId} item={item} />
        ))}
      </S.RoadmapList>
    </S.RoadmapListView>
  );
};

export default RoadmapListView;
