import { PropsWithChildren } from 'react';
import * as S from './Button.styles';

const Button = ({ children }: PropsWithChildren) => {
  return <S.Button>{children}</S.Button>;
};

export default Button;
