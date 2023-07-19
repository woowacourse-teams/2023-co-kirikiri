import * as S from './NavBar.styles';

type NavBarProps = {
  isSwitchOn: boolean;
};

const NavBar = ({ isSwitchOn }: NavBarProps) => {
  return (
    <S.NavBar isNavBarOpen={isSwitchOn}>
      <S.Logo>코끼리끼리</S.Logo>
      <S.SeparateLine />
      <S.Nav>
        <S.Links>
          <div>로드맵</div>
          <div>골룸</div>
          <div>알림</div>
        </S.Links>
        <S.Links>
          <div>우디</div>
          <div>로그아웃</div>
        </S.Links>
      </S.Nav>
    </S.NavBar>
  );
};

export default NavBar;
