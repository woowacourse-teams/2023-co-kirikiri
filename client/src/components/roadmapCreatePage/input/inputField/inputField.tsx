import { ChangeEvent, TextareaHTMLAttributes } from 'react';
import * as S from './inputField.styles';

type InputFieldProps = {
  placeholder?: string;
  handleInputChange: (e: ChangeEvent<HTMLTextAreaElement>) => void;
  maxLength: number;
  checkBlank: () => void;
} & TextareaHTMLAttributes<HTMLTextAreaElement>;

const InputField = (props: InputFieldProps) => {
  const { placeholder = '', handleInputChange, maxLength, checkBlank } = props;

  return (
    <S.Input
      placeholder={placeholder}
      onInput={handleInputChange}
      maxLength={maxLength}
      onBlur={checkBlank}
    />
  );
};

export default InputField;
