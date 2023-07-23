import type { RoadmapItemType } from '@myTypes/roadmap';
import { useParams } from 'react-router-dom';

import RoadmapItem from '@components/_common/roadmapItem/RoadmapItem';
import OpenNodeListButton from '@components/roadmapDetailPage/openNodeListButton/OpenNodeListButton';
import Button from '@components/_common/button/Button';

import { useQuery } from '@tanstack/react-query';
import { getRoadmapById } from '@apis/roadmap';
import QUERY_KEYS from '@constants/@queryKeys/queryKeys';

import * as S from './RoadmapDetail.styles';

const DummyData: RoadmapItemType = {
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
  },
};

const RoadmapDetailPage = () => {
  const { id } = useParams();

  const { data } = useQuery(
    [QUERY_KEYS.roadmap.list, id],
    () => (id ? getRoadmapById(id) : null),
    {
      enabled: !!id,
    }
  );

  // noUnusedLocals 방지를 위한 console
  console.log(data);

  return (
    <S.RoadmapDetailPage>
      <S.PageOnTop>
        <RoadmapItem item={DummyData} hasBorder={false} />
        <OpenNodeListButton />
      </S.PageOnTop>
      <S.RoadmapBody>데이터가 들어갈 예정</S.RoadmapBody>
      <Button>진행중인 골룸 보기</Button>
    </S.RoadmapDetailPage>
  );
};

export default RoadmapDetailPage;
