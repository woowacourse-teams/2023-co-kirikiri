import * as S from './GoalRoomDashboardTodo.styles';
import SVGIcon from '@components/icons/SVGIcon';
import { GoalRoomBrowseResponse } from '@myTypes/goalRoom/remote';
import SingleTodo from '@components/goalRoomDahsboardPage/goalRoomDahsboardTodo/singleTodo/SingleTodo';

const GoalRoomDashboardTodo = ({
  goalRoomData,
}: {
  goalRoomData: GoalRoomBrowseResponse;
}) => {
  const { goalRoomTodos } = goalRoomData;

  return (
    <S.TodoWrapper>
      <div>
        <S.TitleWrapper>
          <h2>투두 리스트</h2>
          <S.CountBox>8</S.CountBox>
        </S.TitleWrapper>

        <button>
          <span>전체보기</span>
          <SVGIcon name='RightArrowIcon' />
        </button>
      </div>

      <div>
        <S.TodoContent>
          {goalRoomTodos.map((todo) => {
            return <SingleTodo key={todo.id} todoContent={todo} />;
          })}
        </S.TodoContent>
      </div>
    </S.TodoWrapper>
  );
};

export default GoalRoomDashboardTodo;
