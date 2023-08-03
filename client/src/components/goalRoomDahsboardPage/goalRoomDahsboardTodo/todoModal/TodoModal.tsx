import * as S from './TodoModal.styles';
import { createPortal } from 'react-dom';
import NewTodoForm from './NewTodoForm';
import { useFetchGoalRoomTodos } from '@hooks/queries/goalRoom';
import SingleTodo from '@components/goalRoomDahsboardPage/goalRoomDahsboardTodo/singleTodo/SingleTodo';

type TodoModalProps = {
  goalRoomId: string;
};
const TodoModal = ({ goalRoomId }: TodoModalProps) => {
  const { goalRoomTodos } = useFetchGoalRoomTodos(goalRoomId);

  const TodoModalContent = (
    <S.ModalWrapper>
      <S.ModalHeader>투두리스트</S.ModalHeader>
      <S.NewTodoText>새로운 투두리스트 등록</S.NewTodoText>
      <NewTodoForm goalRoomId={goalRoomId} />
      {goalRoomTodos.map((todo) => {
        return <SingleTodo todoContent={todo} key={todo.id} />;
      })}
    </S.ModalWrapper>
  );

  return createPortal(TodoModalContent, document.body);
};

export default TodoModal;
