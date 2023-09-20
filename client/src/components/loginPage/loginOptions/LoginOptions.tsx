import { useNaverLogin } from '@/hooks/queries/user';
import SVGIcon from '@components/icons/SVGIcon';
import * as S from './LoginOptions.styles';

type LoginOptionsProps = {
  toggleLoginForm: () => void;
};

const LoginOptions = ({ toggleLoginForm }: LoginOptionsProps) => {
  const { redirectToNaverLoginPage } = useNaverLogin();

  const handleNaverLoginButtonClick = () => {
    redirectToNaverLoginPage();
  };

  return (
    <>
      <S.OathButtonContainer>
        <div>
          <S.OathButton type='naver' onClick={handleNaverLoginButtonClick}>
            <SVGIcon name='NaverIcon' size={12} />
            <span>네이버 로그인</span>
          </S.OathButton>
          <S.OathButton onClick={toggleLoginForm}>
            <SVGIcon name='PersonIcon' size={22} />
            <span>아이디로 로그인하기</span>
          </S.OathButton>
        </div>
      </S.OathButtonContainer>
      <S.InfoText centered>
        계정이 없습니까? <S.StyledLink to='/join'>회원가입하기</S.StyledLink>
      </S.InfoText>
    </>
  );
};

export default LoginOptions;
