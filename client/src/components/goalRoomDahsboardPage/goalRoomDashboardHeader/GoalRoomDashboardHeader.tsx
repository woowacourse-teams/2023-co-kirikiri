import SVGIcon from '@components/icons/SVGIcon';

import * as S from './GoalRoomDashboardHeader.styles';

const GoalRoomDashboardHeader = () => {
  return (
    <header>
      <S.GoalRoomDashboardTitle>hello</S.GoalRoomDashboardTitle>
      <S.GoalRoomLabel>
        <SVGIcon name='PersonIcon' />
        <span>25 / 30 명 참여 중</span>
      </S.GoalRoomLabel>
      <S.GoalRoomLabel>
        <SVGIcon name='CalendarIcon' />
        <span>2023.03.12 ~ 2023.04.21</span>
      </S.GoalRoomLabel>
    </header>
  );
};

export default GoalRoomDashboardHeader;
