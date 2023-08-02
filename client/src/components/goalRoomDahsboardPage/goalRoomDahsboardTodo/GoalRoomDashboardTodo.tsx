import * as S from './GoalRoomDashboardTodo.styles';
import SVGIcon from '@components/icons/SVGIcon';
import { GoalRoomBrowseResponse } from '@myTypes/goalRoom/remote';
import SingleTodo from '@components/goalRoomDahsboardPage/goalRoomDahsboardTodo/singleTodo/SingleTodo';
import {
  DialogBackdrop,
  DialogBox,
  DialogContent,
  DialogTrigger,
} from '@components/_common/dialog/dialog';
import TodoModal from '@components/goalRoomDahsboardPage/goalRoomDahsboardTodo/todoModal/TodoModal';


const GoalRoomDashboardTodo = ({
  goalRoomData,
}: {
  goalRoomData: GoalRoomBrowseResponse;
}) => {
  const { goalRoomTodos } = goalRoomData;

  return (
    <DialogBox>
      <S.TodoWrapper>
        <div>
          <S.TitleWrapper>
            <h2>투두 리스트</h2>
            <S.CountBox>8</S.CountBox>
          </S.TitleWrapper>

          <DialogTrigger asChild>
            <button>
              <span>전체보기</span>
              <SVGIcon name='RightArrowIcon' aria-hidden='true' />
            </button>
          </DialogTrigger>
        </div>

        <div>
          <S.TodoContent>
            {goalRoomTodos.map((todo) => {
              return <SingleTodo key={todo.id} todoContent={todo} />;
            })}
          </S.TodoContent>
        </div>
      </S.TodoWrapper>

      <DialogBackdrop asChild>
        <BackDrop />
      </DialogBackdrop>

      <DialogContent>
        <TodoModal />
      </DialogContent>
    </DialogBox>
  );
};

export default GoalRoomDashboardTodo;
