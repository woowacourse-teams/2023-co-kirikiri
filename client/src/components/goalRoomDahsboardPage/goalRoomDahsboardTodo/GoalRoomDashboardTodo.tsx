import * as S from './GoalRoomDashboardTodo.styles';
import SVGIcon from '@components/icons/SVGIcon';
import { GoalRoomBrowseResponse } from '@myTypes/goalRoom/remote';
import SingleTodo from '@components/goalRoomDahsboardPage/goalRoomDahsboardTodo/singleTodo/SingleTodo';
import { Dialog } from 'ck-util-components';
import TodoModal from '@components/goalRoomDahsboardPage/goalRoomDahsboardTodo/todoModal/TodoModal';
import ToolTip from '@components/_common/toolTip/ToolTip';

type GoalRoomDashboardTodoProps = {
  goalRoomData: GoalRoomBrowseResponse;
  isLeader: boolean;
};

const GoalRoomDashboardTodo = ({
  goalRoomData,
  isLeader,
}: GoalRoomDashboardTodoProps) => {
  const { goalRoomTodos } = goalRoomData;

  return (
    <Dialog>
      <S.TodoWrapper>
        <div>
          <S.TitleWrapper>
            <h2>투두 리스트</h2>
            <ToolTip>
              <p>모임 생성자만 투두리스트를 등록할 수 있습니다.</p>
            </ToolTip>
          </S.TitleWrapper>

          <Dialog.Trigger asChild>
            <button>
              <span>전체보기</span>
              <SVGIcon name='RightArrowIcon' aria-hidden='true' />
            </button>
          </Dialog.Trigger>
        </div>

        <div>
          <S.TodoContent>
            {goalRoomTodos.map((todo) => {
              return <SingleTodo key={todo.id} todoContent={todo} />;
            })}
          </S.TodoContent>
        </div>
      </S.TodoWrapper>

      <Dialog.BackDrop asChild>
        <S.DashboardBackDrop />
      </Dialog.BackDrop>

      <Dialog.Content>
        <TodoModal isLeader={isLeader} />
      </Dialog.Content>
    </Dialog>
  );
};

export default GoalRoomDashboardTodo;
