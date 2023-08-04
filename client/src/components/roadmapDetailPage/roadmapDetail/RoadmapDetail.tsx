import RoadmapItem from '../../_common/roadmapItem/RoadmapItem';
import OpenNodeListButton from '../openNodeListButton/OpenNodeListButton';
import Button from '../../_common/button/Button';
import * as S from './RoadmapDetail.styles';
import useValidParams from '@/hooks/_common/useValidParams';
import { useNavigate } from 'react-router-dom';
import { useRoadmapDetail } from '@/hooks/queries/roadmap';

const RoadmapDetail = () => {
  const { id: roadmapId } = useValidParams<{ id: string }>();
  const navigate = useNavigate();
  const { roadmapInfo } = useRoadmapDetail(Number(roadmapId));

  const moveGoalRoomListPage = () => {
    navigate(`/roadmap/${roadmapId}/goalroom-list`);
  };

  return (
    <S.RoadmapDetail>
      <S.PageOnTop>
        <RoadmapItem item={roadmapInfo} hasBorder={false} roadmapId={Number(roadmapId)} />
        <OpenNodeListButton />
      </S.PageOnTop>
      <S.RoadmapBody>{roadmapInfo.content.content}</S.RoadmapBody>
      <Button onClick={moveGoalRoomListPage}>진행중인 골룸 보기</Button>
    </S.RoadmapDetail>
  );
};

export default RoadmapDetail;
