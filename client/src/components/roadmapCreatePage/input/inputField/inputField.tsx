import { ChangeEvent, TextareaHTMLAttributes } from 'react';
import * as S from './inputField.styles';

type InputFieldProps = {
  handleInputChange: (e: ChangeEvent<HTMLTextAreaElement>) => void;
  validateInput: () => void;
  resetErrorMessage: () => void;
} & TextareaHTMLAttributes<HTMLTextAreaElement>;

const InputField = (props: InputFieldProps) => {
  const { handleInputChange, validateInput, resetErrorMessage, ...restProps } = props;

  return (
    <S.Input
      onInput={handleInputChange}
      onBlur={validateInput}
      onFocus={resetErrorMessage}
      {...restProps}
    />
  );
};

export default InputField;
