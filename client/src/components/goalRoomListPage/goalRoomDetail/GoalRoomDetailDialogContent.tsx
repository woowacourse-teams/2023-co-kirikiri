import { DialogTrigger } from '@/components/_common/dialog/dialog';
import { useGoalRoomDetail, useJoinGoalRoom } from '@hooks/queries/goalRoom';
import { Link } from 'react-router-dom';
import * as S from './goalRoomDetailDialog.styles';

type GoalRoomDetailDialogContentProps = {
  goalRoomId: number;
};

const GoalRoomDetailDialogContent = ({
  goalRoomId,
}: GoalRoomDetailDialogContentProps) => {
  const { joinGoalRoom } = useJoinGoalRoom({ goalRoomId: String(goalRoomId) });
  const { goalRoomInfo } = useGoalRoomDetail(goalRoomId);

  return (
    <>
      <S.Container>
        <S.TitleWrapper>
          <div />
          <S.Title>{goalRoomInfo.name}</S.Title>
          <DialogTrigger asChild>
            <S.CloseButton>X</S.CloseButton>
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
      {goalRoomInfo.isJoined ? (
        <Link to={`/goalroom-dashboard/${goalRoomId}`}>
          <S.EnterGoalRoomButton>모임 대시보드 입장하기</S.EnterGoalRoomButton>
        </Link>
      ) : (
        <S.EnterGoalRoomButton onClick={() => joinGoalRoom()}>
          모임 참여하기
        </S.EnterGoalRoomButton>
      )}
    </>
  );
};

export default GoalRoomDetailDialogContent;
