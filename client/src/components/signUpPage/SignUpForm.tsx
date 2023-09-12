import { MemberJoinRequest } from '@myTypes/user/remote';
import { useSignUp } from '@hooks/queries/user';
import SVGIcon from '@components/icons/SVGIcon';
import logo from '@assets/images/logo.png';
import { SingleCardWrapper } from '@components/_common/SingleCard/SingleCard.styles';
import useFormInput from '@hooks/_common/useFormInput';
import * as S from './SignUpForm.styles';

const SignUpForm = () => {
  const {
    formState: signUpFormData,
    handleInputChange,
    handleSubmit,
    error,
  } = useFormInput<MemberJoinRequest>({
    identifier: '',
    password: '',
    email: '',
    nickname: '',
    genderType: '',
  });

  const { signUp } = useSignUp();

  const onSubmit = () => {
    signUp({
      ...signUpFormData,
      genderType: signUpFormData.genderType.toUpperCase(),
    });
  };

  return (
    <SingleCardWrapper>
      <h1>
        <img src={logo} alt='코끼리 로고' />
      </h1>
      <form onSubmit={handleSubmit(onSubmit)}>
        <S.FormList>
          <S.FormItem>
            <SVGIcon name='PersonIcon' />
            <input name='identifier' onChange={handleInputChange} placeholder='이메일' />
          </S.FormItem>
          <S.FormItem>
            <SVGIcon name='LockIcon' />
            <input
              name='password'
              onChange={handleInputChange}
              placeholder='비밀번호'
              type='password'
            />
          </S.FormItem>
          <S.FormItem>
            <SVGIcon name='EmailIcon' />
            <input
              name='email'
              onChange={handleInputChange}
              placeholder='이메일'
              type='email'
            />
          </S.FormItem>
          <S.FormItem>
            <SVGIcon name='StandingPersonIcon' />
            <input name='nickname' onChange={handleInputChange} placeholder='닉네임' />
          </S.FormItem>
        </S.FormList>

        <S.FormList>
          <S.FormItem>
            <SVGIcon name='GenderIcon' />
            <select name='genderType' onChange={handleInputChange}>
              <option value='' selected disabled>
                성별
              </option>
              <option value='male'>남성</option>
              <option value='female'>여성</option>
            </select>
          </S.FormItem>
        </S.FormList>
        <div style={{ color: 'red' }}>
          {error?.identifier && <p>{error.identifier}</p>}
          {error?.password && <p>{error.password}</p>}
          {error?.nickname && <p>{error.nickname}</p>}
          {error?.genderType && <p>{error.genderType}</p>}
        </div>
        <S.InfoText>
          가입을 진행하시는 것은 우리의 <S.BoldText>이용 약관</S.BoldText> 및{' '}
          <S.BoldText>개인정보 보호 정책</S.BoldText>에 동의하신 것으로 간주됩니다.
        </S.InfoText>
        <S.SubmitButton>회원가입</S.SubmitButton>
        <S.InfoText centered>
          이미 계정이 있습니까? <S.StyledLink to='/login'>로그인하기</S.StyledLink>
        </S.InfoText>
      </form>
    </SingleCardWrapper>
  );
};

export default SignUpForm;
