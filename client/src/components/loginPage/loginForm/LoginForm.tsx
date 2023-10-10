import SVGIcon from '@components/icons/SVGIcon';
import { UserLoginRequest } from '@myTypes/user/remote';
import { useLogin } from '@hooks/queries/user';
import useFormInput from '@hooks/_common/useFormInput';
import * as S from './LoginForm.styles';

const LoginForm = () => {
  const {
    formState: loginData,
    handleInputChange,
    handleSubmit,
  } = useFormInput<UserLoginRequest>({
    identifier: '',
    password: '',
  });

  const { login } = useLogin();

  const onSubmit = () => {
    login(loginData);
  };

  return (
    <S.LoginForm onSubmit={handleSubmit(onSubmit)}>
      <S.FormItemContainer>
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
      </S.FormItemContainer>
      <S.SubmitButton>로그인하기</S.SubmitButton>
    </S.LoginForm>
  );
};

export default LoginForm;
