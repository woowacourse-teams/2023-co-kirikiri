import SVGIcon from '@components/icons/SVGIcon';

import * as S from './GoalRoomDashboardHeader.styles';
import recruitmentStatus from '@constants/goalRoom/recruitmentStatus';
import { GoalRoomBrowseResponse } from '@myTypes/goalRoom/remote';
import { useUserInfoContext } from '@components/_providers/UserInfoProvider';
import isTodayOrAfter from '@utils/_common/isTodayOrAfter';
import { useStartGoalRoom } from '@hooks/queries/goalRoom';
import { useGoalRoomDashboardContext } from '@/context/goalRoomDashboardContext';

type GoalRoomDashboardHeaderProps = {
  goalRoomData: GoalRoomBrowseResponse;
};

const GoalRoomDashboardHeader = ({ goalRoomData }: GoalRoomDashboardHeaderProps) => {
  const {
    name,
    status,
    currentMemberCount,
    limitedMemberCount,
    startDate,
    endDate,
    leaderId,
  } = goalRoomData;

  const { goalroomId } = useGoalRoomDashboardContext();
  const { userInfo } = useUserInfoContext();

  const { startGoalRoom } = useStartGoalRoom(goalroomId);

  const isLeader = userInfo.id === leaderId;

  const isStartButtonVisible =
    isLeader && isTodayOrAfter(startDate) && status === 'RECRUITING';

  const handleGoalRoomStartButton = () => {
    startGoalRoom();
  };

  return (
    <header>
      <S.GoalRoomDashboardTitle>{name}</S.GoalRoomDashboardTitle>
      {isStartButtonVisible && (
        <S.GoalRoomStartButton onClick={handleGoalRoomStartButton}>
          골룸 시작하기
        </S.GoalRoomStartButton>
      )}
      <S.GoalRoomLabel>
        <SVGIcon name='ITIcon' />
        <span>{recruitmentStatus[status]}</span>
      </S.GoalRoomLabel>
      <S.GoalRoomLabel>
        <SVGIcon name='PersonIcon' />
        <span>
          {currentMemberCount} / {limitedMemberCount} 명 참여 중
        </span>
      </S.GoalRoomLabel>
      <S.GoalRoomLabel>
        <SVGIcon name='CalendarIcon' />
        <span>
          {startDate} ~ {endDate}
        </span>
      </S.GoalRoomLabel>
    </header>
  );
};

export default GoalRoomDashboardHeader;
