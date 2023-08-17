import { GoalRoomParticipantsResponse } from '@myTypes/goalRoom/remote';
import * as S from './GoalRoomRankingModal.styles';

type GoalRoomRankingModalProps = {
  userRanking: GoalRoomParticipantsResponse;
};

const GoalRoomRankingModal = ({ userRanking }: GoalRoomRankingModalProps) => {
  return (
    <S.RankingUserWrapper>
      <S.RankingTitle>골룸 유저 랭킹</S.RankingTitle>

      <ul>
        {userRanking.map((user, idx) => {
          return (
            <S.RankingRow key={user.memberId}>
              <S.RankingUserNickname>{idx + 1}. </S.RankingUserNickname>
              <S.RankingUserImage src={user.imagePath} alt='참여자 프로필 이미지' />
              <S.RankingUserNickname>{user.nickname}</S.RankingUserNickname>
            </S.RankingRow>
          );
        })}
      </ul>
    </S.RankingUserWrapper>
  );
};

export default GoalRoomRankingModal;
