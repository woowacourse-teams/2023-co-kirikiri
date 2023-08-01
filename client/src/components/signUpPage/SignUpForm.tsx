import { FormEvent } from 'react';
import { MemberJoinRequest } from '@myTypes/user/remote';
import { useSignUp } from '@hooks/queries/user';
import SVGIcon from '@components/icons/SVGIcon';
import logo from '@assets/images/logo.png';
import { SingleCardWrapper } from '@components/_common/SingleCard/SingleCard.styles';
import useFormInput from '@hooks/_common/useFormInput';
import * as S from './SignUpForm.styles';

const SignUpForm = () => {
  const { formState: signUpFormData, handleInputChange } =
    useFormInput<MemberJoinRequest>({
      identifier: '',
      password: '',
      nickname: '',
      phoneNumber: '',
      genderType: '',
      birthday: '',
    });

  const { signUp } = useSignUp();

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
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
      <form onSubmit={handleSubmit}>
        <S.FormList>
          <S.FormItem>
            <SVGIcon name='PersonIcon' />
            <input name='identifier' onChange={handleInputChange} placeholder='아이디' />
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
            <SVGIcon name='StandingPersonIcon' />
            <input name='nickname' onChange={handleInputChange} placeholder='닉네임' />
          </S.FormItem>
        </S.FormList>

        <S.FormList>
          <S.FormItem>
            <SVGIcon name='PhoneIcon' />
            <input
              name='phoneNumber'
              onChange={handleInputChange}
              placeholder='휴대전화번호 ex) 01012345678'
            />
          </S.FormItem>
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
          <S.FormItem>
            <SVGIcon name='CalendarIcon' />
            <input
              name='birthday'
              onChange={handleInputChange}
              placeholder='생년월일 6자리 ex) 950101'
              type='number'
            />
          </S.FormItem>
        </S.FormList>
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
