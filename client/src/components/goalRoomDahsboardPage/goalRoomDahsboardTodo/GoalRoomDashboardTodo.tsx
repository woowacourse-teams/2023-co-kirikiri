import * as S from './GoalRoomDashboardTodo.styles';
import SVGIcon from '@components/icons/SVGIcon';

const GoalRoomDashboardTodo = () => {
  return (
    <S.TodoWrapper>
      <div>
        <h2>투두 리스트</h2>
        <button>
          <span>전체보기</span>
          <SVGIcon name='RightArrowIcon' />
        </button>
      </div>
      <div>투두</div>
    </S.TodoWrapper>
  );
};

export default GoalRoomDashboardTodo;
