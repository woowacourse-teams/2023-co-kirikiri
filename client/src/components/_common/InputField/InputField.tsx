import { HandleInputChangeType } from '@hooks/_common/useFormInput';
import { ReactNode } from 'react';
import ToolTip from '../toolTip/ToolTip';
import * as S from './InputField.styles';

type InputFieldProps = {
  name: string;
  value: string;
  onChange: HandleInputChangeType;
  label?: string;
  type?: 'text' | 'date' | 'textarea' | 'number';
  size?: 'small' | 'normal';
  isRequired?: boolean;
  description?: string;
  placeholder?: string;
  errorMessage?: string;
  style?: { [key: string]: string };
  toolTip?: ReactNode;
};

const InputField = ({ ...props }: InputFieldProps) => {
  return (
    <S.InputField style={props.style}>
      <S.FieldHeader size={props.size}>
        <S.Label htmlFor={props.name} size={props.size}>
          <div>
            {props.label}
            {props.isRequired && <span>*</span>}
          </div>
          {props.toolTip && <ToolTip>{props.toolTip}</ToolTip>}
        </S.Label>
        {props.description && <S.Description>{props.description}</S.Description>}
      </S.FieldHeader>
      <S.InputBox size={props.size} type={props.type}>
        {props.type === 'textarea' ? (
          <textarea
            id={props.name}
            name={props.name}
            placeholder={props.placeholder}
            value={props.value}
            onChange={props.onChange}
          />
        ) : (
          <input
            id={props.name}
            name={props.name}
            placeholder={props.placeholder}
            value={props.value}
            type={props.type === 'number' ? 'text' : props.type || 'text'}
            onChange={props.onChange}
          />
        )}
        {props.errorMessage && (
          <S.ErrorMessage size={props.size}>{props.errorMessage}</S.ErrorMessage>
        )}
      </S.InputBox>
    </S.InputField>
  );
};

export default InputField;
