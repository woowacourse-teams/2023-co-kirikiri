import { PropsWithChildren } from 'react';
import * as S from './PageLayout.styles';

const Layout = ({ children }: PropsWithChildren) => {
  return (
    <S.Layout>
      <S.NavBar />
      {children}
    </S.Layout>
  );
};

export default Layout;
