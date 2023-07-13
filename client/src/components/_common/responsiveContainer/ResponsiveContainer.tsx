import { PropsWithChildren } from 'react';
import * as S from './ResponsiveContainer.styles';

const ResponsiveContainer = ({ children }: PropsWithChildren) => {
  return <S.Container>{children}</S.Container>;
};

export default ResponsiveContainer;
