import SVGIcon from '@components/icons/SVGIcon';
import { GoalRoomTodo } from '@myTypes/goalRoom/internal';
import * as S from './SingleTodo.styles';
import useHover from '@hooks/_common/useHover';
import { usePostChangeTodoCheckStatus } from '@hooks/queries/goalRoom';
import useValidParams from '@hooks/_common/useValidParams';
import { GoalRoomDashboardContentParams } from '@components/goalRoomDahsboardPage/goalRoomDashboardContent/GoalRoomDashboardContent';

type SingleTodoProps = {
  todoContent: GoalRoomTodo;
};

const SingleTodo = ({ todoContent }: SingleTodoProps) => {
  const { goalroomId } = useValidParams<GoalRoomDashboardContentParams>();

  const { content, startDate, endDate, check } = todoContent;
  const { isHovered, handleMouseEnter, handleMouseLeave } = useHover();
  const { changeTodoCheckStatus } = usePostChangeTodoCheckStatus({
    goalRoomId: goalroomId,
    todoId: String(todoContent.id),
  });

  const handleTodoCheckButtonClick = () => {
    changeTodoCheckStatus();
  };

  return (
    <S.Todo>
      <S.TodoButton
        onMouseEnter={handleMouseEnter}
        onMouseLeave={handleMouseLeave}
        aria-label='hidden'
        onClick={handleTodoCheckButtonClick}
      >
        <SVGIcon name={check.isChecked || isHovered ? 'CheckedCircle' : 'EmptyCircle'} />
      </S.TodoButton>
      <S.TodoContent>{content}</S.TodoContent>
      <S.TodoDate>
        <S.TodoDateSpan>{startDate} ~</S.TodoDateSpan>
        <S.TodoDateSpan>{endDate}</S.TodoDateSpan>
      </S.TodoDate>
    </S.Todo>
  );
};

export default SingleTodo;
