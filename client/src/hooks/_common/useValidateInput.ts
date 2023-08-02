import { ChangeEvent, useState } from 'react';
import { PatternType } from '@myTypes/roadmap/internal';

export const useValidateInput = <T extends HTMLInputElement | HTMLTextAreaElement>(
  pattern: PatternType
) => {
  const { rule, message } = pattern;
  const [value, setValue] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  const handleInputChange = (e: ChangeEvent<T>) => {
    setValue(e.target.value);
  };

  const controlInputChange = (e: ChangeEvent<T>) => {
    const { value } = e.target;
    if (!rule.test(value)) {
      setErrorMessage(message);

      return;
    }

    setErrorMessage('');
    setValue(value);
  };

  const validateInput = () => {
    if (rule.test(value)) return true;

    setErrorMessage(message);

    return false;
  };

  const resetErrorMessage = () => {
    setErrorMessage('');
  };

  return {
    handleInputChange,
    errorMessage,
    value,
    validateInput,
    controlInputChange,
    resetErrorMessage,
    setErrorMessage,
  };
};
