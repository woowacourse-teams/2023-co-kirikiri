import { RoadmapItemType } from '@myTypes/roadmap/internal';
import RoadmapItem from '@components/_common/roadmapItem/RoadmapItem';
import * as S from './RecommendRoadmaps.styles';

const DummyData: RoadmapItemType[] = [
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
      name: 'IT',
      iconName: 'ITIcon',
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
      name: 'IT',
      iconName: 'ITIcon',
    },
  },
];

const RecommendRoadmaps = () => {
  return (
    <S.RecommendRoadmaps>
      <S.Title>
        우디님의 <span>취향</span> 컨텐츠!
      </S.Title>
      <S.Slider>
        {DummyData.map((item) => (
          <RoadmapItem key={item.roadmapId} item={item} />
        ))}
      </S.Slider>
    </S.RecommendRoadmaps>
  );
};

export default RecommendRoadmaps;
