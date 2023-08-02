import * as S from './NewTodoForm.styles';
import { FormEvent } from 'react';
import useNewTodoFormValidation from '@hooks/goalRoom/useNewTodoFormValidation';

const TodoForm = () => {
  const { formState, validateForm, contentInput, startDateInput, endDateInput } =
    useNewTodoFormValidation();

  const handleSubmit = (event: FormEvent) => {
    event.preventDefault();

    if (validateForm()) {
      console.log(formState, 'formState');
    }
  };

  return (
    <S.AddingTodoForm onSubmit={handleSubmit} aria-label='투두리스트 작성 폼'>
      <S.ContentInputContainer>
        <S.DateInputLabel htmlFor='투두리스트 내용'>내용:</S.DateInputLabel>
        <S.ModalContentInput
          id='투두리스트 내용'
          type='text'
          placeholder='투두리스트 내용을 입력해주세요'
          value={contentInput.value}
          onChange={contentInput.controlInputChange}
          aria-required='true'
        />
        {contentInput.errorMessage && (
          <S.Error role='alert'>{contentInput.errorMessage}</S.Error>
        )}
      </S.ContentInputContainer>
      <S.DateInputContainer>
        <S.DateInputLabel htmlFor='startDate'>시작일:</S.DateInputLabel>
        <S.TodoDateInput
          id='startDate'
          name='startDate'
          type='date'
          value={startDateInput.value}
          onChange={startDateInput.controlInputChange}
          aria-required='true'
        />
        {startDateInput.errorMessage && (
          <S.Error role='alert'>{startDateInput.errorMessage}</S.Error>
        )}
      </S.DateInputContainer>
      <S.DateInputContainer>
        <S.DateInputLabel htmlFor='endDate'>종료일:</S.DateInputLabel>
        <S.TodoDateInput
          id='endDate'
          name='endDate'
          type='date'
          value={endDateInput.value}
          onChange={endDateInput.controlInputChange}
          aria-required='true'
        />
        {endDateInput.errorMessage && (
          <S.Error role='alert'>{endDateInput.errorMessage}</S.Error>
        )}
      </S.DateInputContainer>
      <S.SubmitButton type='submit'>등록</S.SubmitButton>
    </S.AddingTodoForm>
  );
};

export default TodoForm;
