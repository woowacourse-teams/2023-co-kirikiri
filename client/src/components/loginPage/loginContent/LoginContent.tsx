import { useState } from 'react';
import { SingleCardWrapper } from '@components/_common/SingleCard/SingleCard.styles';
import logo from '@assets/images/logo.png';
import { Link } from 'react-router-dom';
import BackButton from '@components/_common/backButton/BackButton';
import LoginOptions from '@components/loginPage/loginOptions/LoginOptions';
import LoginForm from '@components/loginPage/loginForm/LoginForm';

const LoginContent = () => {
  const [isLoginFormVisible, setIsLoginFormVisible] = useState<boolean>(false);

  const toggleLoginForm = () => {
    setIsLoginFormVisible((prev) => !prev);
  };

  return (
    <SingleCardWrapper>
      <Link to='/'>
        <img src={logo} alt='코끼리 로고' />
      </Link>
      {!isLoginFormVisible ? (
        <LoginOptions toggleLoginForm={toggleLoginForm} />
      ) : (
        <LoginForm />
      )}
      {isLoginFormVisible && <BackButton action={toggleLoginForm} />}
    </SingleCardWrapper>
  );
};

export default LoginContent;
