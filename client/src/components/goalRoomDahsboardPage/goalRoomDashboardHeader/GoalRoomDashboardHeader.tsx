import SVGIcon from '@components/icons/SVGIcon';

const GoalRoomDashboardHeader = () => {
  return (
    <header>
      <h1>hello</h1>
      <div>
        <SVGIcon name='PersonIcon' />
        <span>25 / 30 명 참여 중</span>
      </div>
      <div>
        <SVGIcon name='CalendarIcon' />
        <span>2023.03.12 ~ 2023.04.21</span>
      </div>
    </header>
  );
};

export default GoalRoomDashboardHeader;
