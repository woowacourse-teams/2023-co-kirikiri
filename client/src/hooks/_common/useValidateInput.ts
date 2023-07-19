import { ChangeEvent, useState } from 'react';

export const useValidateInput = (pattern: any) => {
  const [value, setValue] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  const handleInputChange = (e: ChangeEvent<HTMLTextAreaElement>) => {
    setValue(e.target.value);
  };

  const controlInputChange = (e: ChangeEvent<HTMLTextAreaElement>) => {
    const { rule, message } = pattern;

    if (!rule.test(e.target.value)) {
      e.target.value = e.target.value.slice(0, -1);
      setErrorMessage(message);

      return;
    }

    setValue(e.target.value);
  };

  const validateInput = () => {
    const { rule, message } = pattern;

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
  };
};
