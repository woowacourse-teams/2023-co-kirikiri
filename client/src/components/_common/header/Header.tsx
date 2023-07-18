import { useSwitch } from '@hooks/_common/useSwitch';
import NavBar from '../navBar/NavBar';
import * as S from './Header.styles';

const Header = () => {
  const { isSwitchOn, toggleSwitch, turnSwitchOff } = useSwitch();

  return (
    <S.HeaderWrapper>
      <S.Header>
        <S.NavBarToggleIcon onClick={toggleSwitch}>❏</S.NavBarToggleIcon>
        <S.Logo>코끼리끼리</S.Logo>
      </S.Header>
      <NavBar isSwitchOn={isSwitchOn} />
      {isSwitchOn && <S.NavBarOverlay onClick={turnSwitchOff} />}
    </S.HeaderWrapper>
  );
};

export default Header;
