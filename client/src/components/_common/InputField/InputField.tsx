import React from 'react';
import * as S from './InputField.styles';

type InputFieldProps = {
  name: string;
  value: string;
  onChange: (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => void;
  label?: string;
  type?: 'text' | 'date' | 'textarea';
  size?: 'small' | 'normal';
  isRequired?: boolean;
  description?: string;
  placeholder?: string;
  errorMessage?: string;
};

const InputField = ({ ...props }: InputFieldProps) => {
  return (
    <S.InputField>
      <S.FieldHeader size={props.size}>
        <S.Label htmlFor={props.name} size={props.size}>
          {props.label}
          {props.isRequired && <span>*</span>}
        </S.Label>
        {props.description && <S.Description>{props.description}</S.Description>}
      </S.FieldHeader>
      <S.InputBox size={props.size}>
        <input
          id={props.name}
          name={props.name}
          placeholder={props.placeholder}
          value={props.value}
          type={props.type || 'text'}
          onChange={props.onChange}
        />
        {props.errorMessage && <S.ErrorMessage>{props.errorMessage}</S.ErrorMessage>}
      </S.InputBox>
    </S.InputField>
  );
};

export default InputField;
