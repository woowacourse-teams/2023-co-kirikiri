import { DialogTrigger } from '@/components/_common/dialog/dialog';
import { useGoalRoomDetail } from '@/hooks/queries/goalRoom';
import * as S from './goalRoomDetailDialog.styles';

type GoalRoomDetailDialogContentProps = {
  closeGoalroomDetail: () => void;
  goalRoomId: number;
};

const GoalRoomDetailDialogContent = ({
  closeGoalroomDetail,
  goalRoomId,
}: GoalRoomDetailDialogContentProps) => {
  const { goalRoomInfo } = useGoalRoomDetail(goalRoomId);

  return (
    <S.Container>
      <S.TitleWrapper>
        <div />
        <S.Title>{goalRoomInfo.name}</S.Title>
        <DialogTrigger asChild>
          <S.CloseButton onClick={closeGoalroomDetail}>X</S.CloseButton>
        </DialogTrigger>
      </S.TitleWrapper>
      <S.Participant>
        <p>{goalRoomInfo.currentMemberCount}</p>/{goalRoomInfo.limitedMemberCount}
      </S.Participant>
      <S.RoadmapContainer>
        <S.RoadmapTitle>🐘 로드맵 둘러보기🐘🐘</S.RoadmapTitle>
        {goalRoomInfo.goalRoomNodes.map((node) => {
          return (
            <S.NodeContainer>
              <S.NodePeriod>
                {node.startDate} ~ {node.endDate}
              </S.NodePeriod>
              <S.NodeTitle>{node.title}</S.NodeTitle>
              <S.FeedCount>인증횟수 {node.checkCount}회</S.FeedCount>
            </S.NodeContainer>
          );
        })}
      </S.RoadmapContainer>
    </S.Container>
  );
};

export default GoalRoomDetailDialogContent;
