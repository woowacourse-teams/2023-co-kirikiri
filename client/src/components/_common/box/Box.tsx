import { PropsWithChildren } from 'react';
import * as S from './Box.styles';

const Box = ({ children }: PropsWithChildren) => {
  return <S.Box>{children}</S.Box>;
};

export default Box;
