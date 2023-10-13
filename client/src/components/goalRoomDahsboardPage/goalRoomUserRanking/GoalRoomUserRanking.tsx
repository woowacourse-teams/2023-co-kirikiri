import * as S from './GoalRoomUserRanking.styles';
import { useFetchGoalRoomParticipants } from '@hooks/queries/goalRoom';
import useValidParams from '@hooks/_common/useValidParams';
import { GoalRoomDashboardContentParams } from '@components/goalRoomDahsboardPage/goalRoomDashboardContent/GoalRoomDashboardContent';
import podiumImg from '@assets/images/podium.png';
import podiumImgAV from '@assets/images/podium.avif';
import { useUserInfoContext } from '@components/_providers/UserInfoProvider';
import {
  DialogBackdrop,
  DialogBox,
  DialogContent,
  DialogTrigger,
} from '@components/_common/dialog/dialog';
import SVGIcon from '@components/icons/SVGIcon';
import GoalRoomRankingModal from '@components/goalRoomDahsboardPage/goalRoomUserRanking/goalRoomRankingModal/GoalRoomRankingModal';
import ToolTip from '@components/_common/toolTip/ToolTip';

const GoalRoomUserRanking = () => {
  const { goalroomId } = useValidParams<GoalRoomDashboardContentParams>();
  const { goalRoomParticipants } = useFetchGoalRoomParticipants(
    goalroomId,
    'ACCOMPLISHMENT_RATE'
  );
  const { userInfo } = useUserInfoContext();

  const userRankCardInfo = goalRoomParticipants.find(
    (participant) => participant.memberId === userInfo.id
  );

  const userRank =
    goalRoomParticipants.findIndex(
      (participant) => participant.memberId === userInfo.id
    ) + 1;

  return (
    <DialogBox>
      <S.CalenderWrapper>
        <div>
          <h2>모임 유저 랭킹</h2>
          <ToolTip>
            <p>
              인증 피드 개수를 기반으로 <br /> 랭킹이 측정됩니다.
            </p>
          </ToolTip>

          <DialogTrigger asChild>
            <button aria-labelledby='유저 랭킹 전체보기'>
              <span>전체보기</span>
              <SVGIcon name='RightArrowIcon' aria-hidden='true' />
            </button>
          </DialogTrigger>
        </div>
        <S.PodiumWrapper>
          <picture>
            <source srcSet={podiumImgAV} />
            <S.PodiumImage src={podiumImg} alt='Podium' loading='lazy' />
          </picture>
          {goalRoomParticipants.slice(0, 3).map((participant, index) => {
            return (
              <S.Participant key={participant.memberId} position={index}>
                <S.UserInfoLabel>
                  <img
                    src={participant.imagePath}
                    alt={`${participant.nickname} 의 프로필 이미지`}
                    loading='lazy'
                  />
                  <p>{participant.nickname}</p>
                </S.UserInfoLabel>
              </S.Participant>
            );
          })}
          {userRankCardInfo && (
            <S.Card key={userRankCardInfo.memberId}>
              <S.UserInfoLabel>
                <p>{userRank}위</p>
                <img
                  src={userRankCardInfo.imagePath}
                  alt={`${userRankCardInfo.nickname} 의 프로필 이미지`}
                />
                <p>{userRankCardInfo.nickname}</p>
                <p>{userRankCardInfo.accomplishmentRate.toFixed(1)}%</p>
              </S.UserInfoLabel>
            </S.Card>
          )}
        </S.PodiumWrapper>
      </S.CalenderWrapper>
      <DialogBackdrop asChild>
        <S.ModalBackdrop />
      </DialogBackdrop>
      <DialogContent>
        <GoalRoomRankingModal userRanking={goalRoomParticipants} />
      </DialogContent>
    </DialogBox>
  );
};

export default GoalRoomUserRanking;
