import logo from '@assets/images/logo.png';
import { Link } from 'react-router-dom';
import BackButton from '@components/_common/backButton/BackButton';
import LoginOptions from '@components/loginPage/loginOptions/LoginOptions';
import LoginForm from '@components/loginPage/loginForm/LoginForm';
import { SingleCardWrapper } from '@components/_common/SingleCard/SingleCard.styles';
import { useSwitch } from '@hooks/_common/useSwitch';

const LoginContent = () => {
  const { isSwitchOn: isLoginFormVisible, toggleSwitch: toggleLoginForm } = useSwitch();

  return (
    <SingleCardWrapper>
      <Link to='/'>
        <img src={logo} alt='코끼리 로고' />
      </Link>
      {isLoginFormVisible ? (
        <LoginForm />
      ) : (
        <LoginOptions toggleLoginForm={toggleLoginForm} />
      )}
      {isLoginFormVisible && <BackButton action={toggleLoginForm} />}
    </SingleCardWrapper>
  );
};

export default LoginContent;
