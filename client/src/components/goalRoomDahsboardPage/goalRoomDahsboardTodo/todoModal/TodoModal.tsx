import * as S from './TodoModal.styles';
import { createPortal } from 'react-dom';
import NewTodoForm from './NewTodoForm';
import { useFetchGoalRoomTodos } from '@hooks/queries/goalRoom';
import SingleTodo from '@components/goalRoomDahsboardPage/goalRoomDahsboardTodo/singleTodo/SingleTodo';
import { useGoalRoomDashboardContext } from '@/context/goalRoomDashboardContext';

type TodoModalProps = {
  isLeader: boolean;
};

const TodoModal = ({ isLeader }: TodoModalProps) => {
  const { goalroomId } = useGoalRoomDashboardContext();
  const { goalRoomTodos } = useFetchGoalRoomTodos(goalroomId);

  const TodoModalContent = (
    <S.ModalWrapper>
      <S.ModalHeader>투두리스트</S.ModalHeader>
      {isLeader && (
        <>
          <S.NewTodoText>새로운 투두리스트 등록</S.NewTodoText>
          <NewTodoForm />
        </>
      )}
      {goalRoomTodos.map((todo) => {
        return <SingleTodo todoContent={todo} key={todo.id} />;
      })}
    </S.ModalWrapper>
  );

  return createPortal(TodoModalContent, document.body);
};

export default TodoModal;
