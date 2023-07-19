import { ChangeEvent, useState } from 'react';

export const useValidateInput = (patterns: any) => {
  const [value, setValue] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  const validateInput = (value: string) => {
    patterns.every((pattern: any) => {
      const { rule, message } = pattern;
      if (!rule.test(value)) {
        setErrorMessage(message);
        return false;
      }
      setErrorMessage('');
      return true;
    });
  };

  const handleInputChange = (e: ChangeEvent<HTMLTextAreaElement>) => {
    setValue(e.target.value);

    validateInput(e.target.value);
  };

  const checkBlank = () => {
    if (value.length === 0) {
      setErrorMessage('필수 입력란 입니다');
    }
  };

  return { handleInputChange, checkBlank, errorMessage, value };
};
