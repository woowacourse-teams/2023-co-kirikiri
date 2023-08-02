import { useValidateInput } from '@hooks/_common/useValidateInput';
import {
  TODO_CONTENT_MAX_LENGTH,
  TODO_END_DATE,
  TODO_START_DATE,
} from '@constants/goalRoom/regex';
import { useEffect, useState } from 'react';

const useNewTodoFormValidation = () => {
  const contentInput = useValidateInput(TODO_CONTENT_MAX_LENGTH);
  const startDateInput = useValidateInput(TODO_START_DATE);
  const endDateInput = useValidateInput(TODO_END_DATE);

  const [formState, setFormState] = useState({});

  useEffect(() => {
    setFormState({
      content: contentInput.value,
      startDate: startDateInput.value,
      endDate: endDateInput.value,
    });
  }, [contentInput.value, startDateInput.value, endDateInput.value]);

  const validateForm = () => {
    let isValid = true;

    if (!startDateInput.value) {
      startDateInput.setErrorMessage('시작일을 기입해주세요.');
      isValid = false;
    }

    if (!endDateInput.value) {
      endDateInput.setErrorMessage('종료일을 기입해주세요.');
      isValid = false;
    }

    if (isValid) {
      const startDate = new Date(startDateInput.value);
      const endDate = new Date(endDateInput.value);

      if (startDate >= endDate) {
        endDateInput.setErrorMessage('종료일이 시작일보다 이전입니다.');
        isValid = false;
      }
    }

    return isValid;
  };

  return { formState, validateForm, contentInput, startDateInput, endDateInput };
};

export default useNewTodoFormValidation;
