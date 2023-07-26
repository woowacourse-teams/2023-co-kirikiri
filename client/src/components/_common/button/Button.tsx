import { PropsWithChildren } from 'react';
import * as S from './Button.styles';

type ButtonProps = {
  variant?: 'primary';
} & PropsWithChildren;

const Button = ({ children, variant = 'primary' }: ButtonProps) => {
  return <S.Button variant={variant}>{children}</S.Button>;
};

export default Button;
