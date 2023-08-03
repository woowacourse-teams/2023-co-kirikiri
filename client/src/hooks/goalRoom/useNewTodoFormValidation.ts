import { useValidateInput } from '@hooks/_common/useValidateInput';
import {
  TODO_CONTENT_MAX_LENGTH,
  TODO_END_DATE,
  TODO_START_DATE,
} from '@constants/goalRoom/regex';
import { useEffect, useState } from 'react';
import { newTodoPayload } from '@myTypes/goalRoom/remote';

const useNewTodoFormValidation = () => {
  const contentInput = useValidateInput(TODO_CONTENT_MAX_LENGTH);
  const startDateInput = useValidateInput(TODO_START_DATE);
  const endDateInput = useValidateInput(TODO_END_DATE);

  const [formState, setFormState] = useState<Omit<newTodoPayload, 'goalRoomId'>>({
    content: '',
    startDate: '',
    endDate: '',
  });

  useEffect(() => {
    setFormState({
      content: contentInput.value,
      startDate: startDateInput.value,
      endDate: endDateInput.value,
    });
  }, [contentInput.value, startDateInput.value, endDateInput.value]);

  const validateForm = () => {
    if (!startDateInput.value) {
      startDateInput.setErrorMessage('시작일을 기입해주세요.');
      return false;
    }

    if (!endDateInput.value) {
      endDateInput.setErrorMessage('종료일을 기입해주세요.');
      return false;
    }

    const startDate = new Date(startDateInput.value);
    const endDate = new Date(endDateInput.value);

    if (startDate >= endDate) {
      endDateInput.setErrorMessage('종료일이 시작일보다 이전입니다.');
      return false;
    }

    return true;
  };

  return { formState, validateForm, contentInput, startDateInput, endDateInput };
};

export default useNewTodoFormValidation;
