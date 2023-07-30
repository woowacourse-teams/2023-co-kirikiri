import SVGIcon from '@components/icons/SVGIcon';

import * as S from './GoalRoomDashboardHeader.styles';
import { GoalRoomRecruitmentStatus } from '@myTypes/goalRoom/internal';
import recruitmentStatus from '@constants/goalRoom/recruitmentStatus';

type GoalRoomDashboardHeaderProps = {
  name: string;
  status: GoalRoomRecruitmentStatus;
  currentMemberCount: number;
  initMemberCount: number;
  startDate: string;
  endDate: string;
};

const GoalRoomDashboardHeader = ({
  name,
  status,
  currentMemberCount,
  initMemberCount,
  startDate,
  endDate,
}: GoalRoomDashboardHeaderProps) => {
  return (
    <header>
      <S.GoalRoomDashboardTitle>{name}</S.GoalRoomDashboardTitle>
      <S.GoalRoomLabel>
        <SVGIcon name='ITIcon' />
        <span>{recruitmentStatus[status]}</span>
      </S.GoalRoomLabel>
      <S.GoalRoomLabel>
        <SVGIcon name='PersonIcon' />
        <span>
          {currentMemberCount} / {initMemberCount} 명 참여 중
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
