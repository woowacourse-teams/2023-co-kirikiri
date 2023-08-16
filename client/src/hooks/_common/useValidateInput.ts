import { ChangeEvent, useState } from 'react';
import { PatternType } from '@myTypes/roadmap/internal';

export const useValidateInput = <
  T extends HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
>(
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
    setValue(value);

    if (!rule.test(value)) {
      setErrorMessage(message);
    } else {
      setErrorMessage('');
    }
  };

  const validateInput = () => {
    if (rule.test(value)) return true;

    setErrorMessage(message);

    return false;
  };

  const resetErrorMessage = () => {
    setErrorMessage('');
  };

  const resetValue = () => {
    setValue('');
  };

  return {
    handleInputChange,
    errorMessage,
    value,
    validateInput,
    controlInputChange,
    resetErrorMessage,
    setErrorMessage,
    resetValue,
  };
};
