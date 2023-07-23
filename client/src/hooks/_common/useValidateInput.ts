import { PatternType } from '@/myTypes/roadmap/roadmapCreate';
import { ChangeEvent, useState } from 'react';

export const useValidateInput = (pattern: PatternType) => {
  const { rule, message } = pattern;
  const [value, setValue] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  const handleInputChange = (e: ChangeEvent<HTMLTextAreaElement>) => {
    setValue(e.target.value);
  };

  const controlInputChange = ({
    target: { value },
  }: ChangeEvent<HTMLTextAreaElement>) => {
    if (!rule.test(value)) {
      setErrorMessage(message);

      return;
    }

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
  };
};
