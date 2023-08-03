import type { RoadmapItemType } from '@myTypes/roadmap/internal';

import RoadmapItem from '../../_common/roadmapItem/RoadmapItem';
import OpenNodeListButton from '../openNodeListButton/OpenNodeListButton';
import Button from '../../_common/button/Button';
import * as S from './RoadmapDetail.styles';
import useValidParams from '@/hooks/_common/useValidParams';
import { useNavigate } from 'react-router-dom';

const DummyData: RoadmapItemType = {
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
};

const RoadmapDetail = () => {
  const { id: roadmapId } = useValidParams<{ id: string }>();
  const navigate = useNavigate();

  const moveGoalRoomListPage = () => {
    navigate(`/roadmap/${roadmapId}/goalroom-list`);
  };

  return (
    <S.RoadmapDetail>
      <S.PageOnTop>
        <RoadmapItem item={DummyData} hasBorder={false} roadmapId={Number(roadmapId)} />
        <OpenNodeListButton />
      </S.PageOnTop>
      <S.RoadmapBody>데이터가 들어갈 예정</S.RoadmapBody>
      <Button onClick={moveGoalRoomListPage}>진행중인 골룸 보기</Button>
    </S.RoadmapDetail>
  );
};

export default RoadmapDetail;
