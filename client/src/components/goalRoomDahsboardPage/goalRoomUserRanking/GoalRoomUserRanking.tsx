import * as S from './GoalRoomUserRanking.styles';
import { useFetchGoalRoomParticipants } from '@hooks/queries/goalRoom';
import useValidParams from '@hooks/_common/useValidParams';
import { GoalRoomDashboardContentParams } from '@components/goalRoomDahsboardPage/goalRoomDashboardContent/GoalRoomDashboardContent';
import { BASE_URL } from '@apis/axios/client';
import podiumImg from '@assets/images/podium.png';

const GoalRoomUserRanking = () => {
  const { goalroomId } = useValidParams<GoalRoomDashboardContentParams>();
  const { goalRoomParticipants } = useFetchGoalRoomParticipants(
    goalroomId,
    'ACCOMPLISHMENT_RATE'
  );

  return (
    <S.CalenderWrapper>
      <div>
        <h2>골룸 유저 랭킹</h2>
      </div>
      <S.PodiumWrapper>
        <S.PodiumImage src={podiumImg} alt='Podium' />
        {goalRoomParticipants.slice(0, 3).map((participant, index) => {
          return (
            <S.Participant key={participant.memberId} position={index}>
              <S.UserInfoLabel>
                <img
                  src={BASE_URL + participant.imagePath}
                  alt={`${participant.nickname} 의 프로필 이미지`}
                />
                <p>{participant.nickname}</p>
              </S.UserInfoLabel>
            </S.Participant>
          );
        })}
      </S.PodiumWrapper>
      <div>
        {goalRoomParticipants.slice(3).map((participant) => (
          <S.Card key={participant.memberId}>
            <S.UserInfoLabel>
              <img
                src={BASE_URL + participant.imagePath}
                alt={`${participant.nickname} 의 프로필 이미지`}
              />
              <p>{participant.nickname}</p>
              <p>{participant.accomplishmentRate.toFixed(1)}%</p>
            </S.UserInfoLabel>
          </S.Card>
        ))}
      </div>
    </S.CalenderWrapper>
  );
};

export default GoalRoomUserRanking;
