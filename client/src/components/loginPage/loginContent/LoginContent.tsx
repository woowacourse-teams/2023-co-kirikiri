import logo from '@assets/images/logo.png';
import logoAV from '@assets/images/logo.avif';
import { Link } from 'react-router-dom';
import BackButton from '@components/_common/backButton/BackButton';
import LoginOptions from '@components/loginPage/loginOptions/LoginOptions';
import LoginForm from '@components/loginPage/loginForm/LoginForm';
import { useSwitch } from '@hooks/_common/useSwitch';

import * as S from './LoginContent.styles';

const LoginContent = () => {
  const { isSwitchOn: isLoginFormVisible, toggleSwitch: toggleLoginForm } = useSwitch();

  return (
    <S.LoginContentWrapper>
      <Link to='/roadmap-list'>
        <picture>
          <source srcSet={logoAV} />
          <img src={logo} alt='코끼리 로고' />
        </picture>
      </Link>
      {isLoginFormVisible ? (
        <LoginForm />
      ) : (
        <LoginOptions toggleLoginForm={toggleLoginForm} />
      )}
      {isLoginFormVisible && <BackButton action={toggleLoginForm} />}
    </S.LoginContentWrapper>
  );
};

export default LoginContent;
