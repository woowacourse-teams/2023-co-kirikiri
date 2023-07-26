import * as S from './GoalRoomDashboardCalender.styles';
import SVGIcon from '@components/icons/SVGIcon';

const GoalRoomDashboardCalender = () => {
  return (
    <S.CalenderWrapper>
      <div>
        <h2>캘린더</h2>
        <button>
          <span>전체보기</span>
          <SVGIcon name='RightArrowIcon' />
        </button>
      </div>
      <div>켈린더</div>
    </S.CalenderWrapper>
  );
};

export default GoalRoomDashboardCalender;
