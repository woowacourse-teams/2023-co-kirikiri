import { RoadmapItemType } from '@myTypes/roadmap/internal';
import RoadmapItem from '@components/_common/roadmapItem/RoadmapItem';
import * as S from './RecommendRoadmaps.styles';

const DummyData: RoadmapItemType[] = [
  {
    roadmapId: 1,
    roadmapTitle: '로드맵 제목1',
    introduction: '로드맵 소개글1',
    difficulty: 'NORMAL',
    recommendedRoadmapPeriod: 10,
    createdAt: [2023, 8, 2, 19, 17, 28, 81052591],
    creator: {
      id: 1,
      name: '코끼리',
    },
    category: {
      id: 1,
      name: '여행',
    },
    tags: [
      {
        id: 1,
        name: '태그1',
      },
      {
        id: 2,
        name: '태그2',
      },
    ],
  },
  {
    roadmapId: 2,
    roadmapTitle: '로드맵 제목2',
    introduction: '로드맵 소개글2',
    difficulty: 'DIFFICULT',
    recommendedRoadmapPeriod: 7,
    createdAt: [2023, 8, 2, 19, 17, 28, 81052591],
    creator: {
      id: 2,
      name: '끼리코',
    },
    category: {
      id: 2,
      name: 'IT',
    },
    tags: [
      {
        id: 1,
        name: '태그1',
      },
      {
        id: 2,
        name: '태그2',
      },
    ],
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
