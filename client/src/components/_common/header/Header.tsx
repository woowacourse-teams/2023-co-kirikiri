import { useState } from 'react';
import NavBar from '../navBar/NavBar';
import * as S from './Header.styles';

const Header = () => {
  const [isNavBarOpen, setIsNavBarOpen] = useState(false);

  const toggleNavBarState = () => {
    setIsNavBarOpen((prev) => !prev);
  };

  return (
    <S.HeaderWrapper>
      <S.Header>
        <S.NavBarToggleIcon onClick={toggleNavBarState}>❏</S.NavBarToggleIcon>
        <S.Logo>코끼리끼리</S.Logo>
      </S.Header>
      <NavBar isNavBarOpen={isNavBarOpen} />
      {isNavBarOpen && <S.CloseNavBackground onClick={toggleNavBarState} />}
    </S.HeaderWrapper>
  );
};

export default Header;
