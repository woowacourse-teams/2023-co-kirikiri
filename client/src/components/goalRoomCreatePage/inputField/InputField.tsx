import { PropsWithChildren } from 'react';
import * as S from './InputField.styles';

export type InputType = 'normal' | 'small';

type InputFieldProps = {
  label: string;
  description?: string;
  isRequired?: boolean;
  errorMessage?: any;
  type?: InputType;
} & PropsWithChildren;

const InputField = (props: InputFieldProps) => {
  const {
    label,
    description,
    isRequired,
    errorMessage,
    children,
    type = 'normal',
  } = props;

  return (
    <S.InputField htmlFor={label}>
      <div>
        {label && (
          <S.Label isRequired={Boolean(isRequired)} type={type}>
            {label}
          </S.Label>
        )}
        {description && <S.Description>{description}</S.Description>}
      </div>
      <S.ChildrenWrapper type={type}>{children}</S.ChildrenWrapper>
      {errorMessage && <p>에러가 들어갈 예정</p>}
    </S.InputField>
  );
};

export default InputField;
