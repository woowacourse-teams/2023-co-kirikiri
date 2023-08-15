import { CreateGoalRoomRequest } from '@myTypes/goalRoom/remote';
import InputField from '@components/_common/InputField/InputField';
import { HandleInputChangeType, FormErrorType } from '@hooks/_common/useFormInput';
import * as S from './TodoListSection.styles';

type TodoListSectionProps = {
  formState: CreateGoalRoomRequest;
  handleInputChange: HandleInputChangeType;
  error: FormErrorType;
};

const TodoListSection = ({
  formState,
  handleInputChange,
  error,
}: TodoListSectionProps) => {
  return (
    <div>
      <S.DateConfig>
        <InputField
          label='수행 시작 일자'
          isRequired
          size='small'
          name='goalRoomTodo[startDate]'
          type='date'
          value={formState.goalRoomTodo.startDate || ''}
          onChange={handleInputChange}
          errorMessage={error?.['goalRoomTodo[startDate]']}
        />
        <InputField
          label='수행 종료 일자'
          isRequired
          size='small'
          name='goalRoomTodo[endDate]'
          type='date'
          value={formState.goalRoomTodo.endDate || ''}
          onChange={handleInputChange}
          errorMessage={error?.['goalRoomTodo[endDate]']}
        />
      </S.DateConfig>
      <InputField
        type='textarea'
        name='goalRoomTodo[content]'
        value={formState.goalRoomTodo.content || ''}
        placeholder='추가할 작업을 설명해주세요'
        onChange={handleInputChange}
        errorMessage={error?.['goalRoomTodo[content]']}
      />
    </div>
  );
};

export default TodoListSection;
