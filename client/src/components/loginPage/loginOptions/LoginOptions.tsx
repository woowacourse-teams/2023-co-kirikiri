import SVGIcon from '@components/icons/SVGIcon';
import * as S from './LoginOptions.styles';

type LoginOptionsProps = {
  toggleLoginForm: () => void;
};

const LoginOptions = ({ toggleLoginForm }: LoginOptionsProps) => {
  return (
    <>
      <S.OathButtonContainer>
        <div>
          <S.OathButton type='kakao'>
            <SVGIcon name='KakaoIcon' />
            카카오톡으로 3초 만에 로그인하기
          </S.OathButton>
          <S.OathButton type='google'>
            <SVGIcon name='GoogleIcon' /> 구글로 로그인하기
          </S.OathButton>
          <S.OathButton onClick={toggleLoginForm}>
            <SVGIcon name='PersonIcon' /> 아이디로 로그인하기
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
