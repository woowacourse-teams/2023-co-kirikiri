import { PropsWithChildren } from 'react';
import * as S from './Button.styles';

type ButtonProps = {
  variant?: 'primary';
  onClick?: () => void;
} & PropsWithChildren;

const Button = ({ children, variant = 'primary', onClick }: ButtonProps) => {
  return (
    <S.Button variant={variant} onClick={onClick} data-testid='button'>
      {children}
    </S.Button>
  );
};

export default Button;
