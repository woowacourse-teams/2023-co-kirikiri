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
import styled from 'styled-components';
import TodoModal from '@components/goalRoomDahsboardPage/goalRoomDahsboardTodo/todoModal/TodoModal';

const BackDrop = styled.div`
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;

  background-color: rgba(220, 220, 220, 0.44);
`;

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

      <DialogContent>
        <TodoModal />
      </DialogContent>

      <DialogBackdrop asChild>
        <BackDrop />
      </DialogBackdrop>
    </DialogBox>
  );
};

export default GoalRoomDashboardTodo;
