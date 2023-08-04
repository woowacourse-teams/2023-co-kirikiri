import SVGIcon from '@components/icons/SVGIcon';

import * as S from './GoalRoomDashboardHeader.styles';
import recruitmentStatus from '@constants/goalRoom/recruitmentStatus';
import { GoalRoomBrowseResponse } from '@myTypes/goalRoom/remote';

type GoalRoomDashboardHeaderProps = {
  goalRoomData: GoalRoomBrowseResponse;
};

const GoalRoomDashboardHeader = ({ goalRoomData }: GoalRoomDashboardHeaderProps) => {
  const { name, status, currentMemberCount, limitedMemberCount, startDate, endDate } =
    goalRoomData;

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
