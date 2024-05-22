import { MemberJoinRequest } from '@myTypes/user/remote';
import { useSignUp } from '@hooks/queries/user';
import SVGIcon from '@components/icons/SVGIcon';
import logo from '@assets/images/logo.png';
import logoAV from '@assets/images/logo.avif';
import { SingleCardWrapper } from '@components/_common/SingleCard/SingleCard.styles';
import * as S from './SignUpForm.styles';
import { useForm } from 'react-lightweight-form';

const SignUpForm = () => {
  const { register, handleSubmit, errors } = useForm<MemberJoinRequest>();
  const { signUp } = useSignUp();

  const onSubmit = (formData: MemberJoinRequest) => {
    signUp(formData);
  };

  return (
    <SingleCardWrapper>
      <picture>
        <source srcSet={logoAV} />
        <S.Logo src={logo} alt='코끼리 로고' />
      </picture>
      <S.FormWrapper onSubmit={handleSubmit(onSubmit)}>
        <S.FormList>
          <S.FormItem>
            <SVGIcon name='PersonIcon' />
            <input
              {...register('identifier', {
                pattern: {
                  value: /^[a-z0-9]{4,20}$/,
                  message:
                    '-아이디는 영어 소문자와 숫자만 포함할 수 있으며, 4~20자여야 합니다.',
                },
              })}
              placeholder='아이디'
            />
          </S.FormItem>
          <S.FormItem>
            <SVGIcon name='LockIcon' />
            <input
              {...register('password', {
                pattern: {
                  value: /^[a-z0-9!@#$%^&*()~]{8,15}$/,
                  message:
                    '-비밀번호는 8~15자리여야 하며, 영어 소문자, 숫자, [!,@,#,$,%,^,&,*,(,),~] 특수문자만 포함해야 합니다.',
                },
              })}
              placeholder='비밀번호'
              type='password'
            />
          </S.FormItem>
          <S.FormItem>
            <SVGIcon name='EmailIcon' />
            <input
              {...register('email', {
                pattern: {
                  value:
                    /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i,
                  message: '-유효하지 않은 이메일 형식입니다.',
                },
              })}
              placeholder='이메일'
              type='email'
            />
          </S.FormItem>
          <S.FormItem>
            <SVGIcon name='StandingPersonIcon' />
            <input
              {...register('nickname', {
                pattern: {
                  value: /^.{2,8}$/,
                  message: '-닉네임은 2~8자리여야 합니다.',
                },
              })}
              placeholder='닉네임'
            />
          </S.FormItem>
        </S.FormList>

        <S.FormList>
          <S.FormItem>
            <SVGIcon name='GenderIcon' />
            <select
              {...register('genderType', {
                pattern: {
                  value: /^(male|female)$/,
                  message: "-성별은 '남자' 또는 '여자'만 선택 가능합니다.",
                },
                setValueAs: (inputValue) => inputValue.toUpperCase(),
              })}
            >
              <option value='' selected disabled>
                성별
              </option>
              <option value='male'>남성</option>
              <option value='female'>여성</option>
            </select>
          </S.FormItem>
        </S.FormList>
        <S.ErrorBox>
          {Object.values(errors).map((message: string) => (
            <p>{message}</p>
          ))}
        </S.ErrorBox>
        <S.InfoText>
          가입을 진행하시는 것은 우리의 <S.BoldText>이용 약관</S.BoldText> 및{' '}
          <S.BoldText>개인정보 보호 정책</S.BoldText>에 동의하신 것으로 간주됩니다.
        </S.InfoText>
        <S.SubmitButton>회원가입</S.SubmitButton>
        <S.InfoText centered>
          이미 계정이 있습니까? <S.StyledLink to='/login'>로그인하기</S.StyledLink>
        </S.InfoText>
      </S.FormWrapper>
    </SingleCardWrapper>
  );
};

export default SignUpForm;
