import LoginContent from '@components/loginPage/loginContent/LoginContent';
import * as S from './LoginPage.styles';
import elephantImg from '@assets/images/elephant.png';

const LoginPage = () => {
  return (
    <S.LoginPageWrapper>
      <LoginContent />
      <S.ElephantImage src={elephantImg} alt='코끼리 이미지' />
    </S.LoginPageWrapper>
  );
};

export default LoginPage;
