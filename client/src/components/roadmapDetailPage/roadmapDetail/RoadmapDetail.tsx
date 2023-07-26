import type { RoadmapItemType } from '@myTypes/roadmap';

import RoadmapItem from '../../_common/roadmapItem/RoadmapItem';
import OpenNodeListButton from '../openNodeListButton/OpenNodeListButton';
import Button from '../../_common/button/Button';
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
    iconName: 'LanguageIcon',
  },
};

const RoadmapDetail = () => {
  return (
    <S.RoadmapDetail>
      <S.PageOnTop>
        <RoadmapItem item={DummyData} hasBorder={false} />
        <OpenNodeListButton />
      </S.PageOnTop>
      <S.RoadmapBody>데이터가 들어갈 예정</S.RoadmapBody>
      <Button>진행중인 골룸 보기</Button>
    </S.RoadmapDetail>
  );
};

export default RoadmapDetail;
