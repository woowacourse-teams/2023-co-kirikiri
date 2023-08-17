import RoadmapItem from '../../_common/roadmapItem/RoadmapItem';
import Button from '../../_common/button/Button';
import * as S from './RoadmapDetail.styles';
import useValidParams from '@/hooks/_common/useValidParams';
import { useNavigate } from 'react-router-dom';
import { useRoadmapDetail } from '@/hooks/queries/roadmap';
import RoadmapNodeList from '../roadmapNodeList/RoadmapNodeList';

const RoadmapDetail = () => {
  const { id: roadmapId } = useValidParams<{ id: string }>();
  const navigate = useNavigate();
  const { roadmapInfo } = useRoadmapDetail(Number(roadmapId));

  const moveGoalRoomListPage = () => {
    navigate(`/roadmap/${roadmapId}/goalroom-list`);
  };

  console.log(roadmapInfo);

  return (
    <S.RoadmapDetail>
      <S.PageOnTop>
        <RoadmapItem item={roadmapInfo} hasBorder={false} roadmapId={Number(roadmapId)} />
        <S.RoadmapBody>
          <strong>로드맵 설명</strong> <br />
          {roadmapInfo.content.content === ''
            ? '로드맵에 대한 설명이 없어요🥲'
            : roadmapInfo.content.content}
        </S.RoadmapBody>
      </S.PageOnTop>
      <RoadmapNodeList
        roadmapTitle={roadmapInfo.roadmapTitle}
        nodeInfo={roadmapInfo.content.nodes}
      />
      <Button onClick={moveGoalRoomListPage}>진행중인 골룸 보기</Button>
    </S.RoadmapDetail>
  );
};

export default RoadmapDetail;
