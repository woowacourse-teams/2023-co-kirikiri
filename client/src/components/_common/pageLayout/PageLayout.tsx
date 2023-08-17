import { PropsWithChildren } from 'react';
import * as S from './PageLayout.styles';
import Header from '../header/Header';

const PageLayout = ({ children }: PropsWithChildren) => {
  return (
    <S.Layout>
      <Header />
      <S.ChildrenLayout>{children}</S.ChildrenLayout>
    </S.Layout>
  );
};

export default PageLayout;
