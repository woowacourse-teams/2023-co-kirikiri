import { PropsWithChildren } from 'react';
import NavBar from '@components/_common/navBar/NavBar';
import * as S from './PageLayout.styles';

const PageLayout = ({ children }: PropsWithChildren) => {
  return (
    <S.Layout>
      <NavBar />
      <S.ChildrenLayout>{children}</S.ChildrenLayout>
    </S.Layout>
  );
};

export default PageLayout;
