import SVGIcon from '@components/icons/SVGIcon';
import { UserLoginRequest } from '@myTypes/user/remote';
import { useLogin } from '@hooks/queries/user';
import * as S from './LoginForm.styles';
import { useForm } from 'react-lightweight-form';

const LoginForm = () => {
  const { register, handleSubmit } = useForm<UserLoginRequest>();
  const { login } = useLogin();

  const onSubmit = (formData: UserLoginRequest) => {
    login(formData);
  };

  return (
    <S.LoginForm onSubmit={handleSubmit(onSubmit)}>
      <S.FormItemContainer>
        <S.FormItem>
          <SVGIcon name='PersonIcon' />
          <input {...register('identifier')} placeholder='아이디' />
        </S.FormItem>
        <S.FormItem>
          <SVGIcon name='LockIcon' />
          <input {...register('password')} placeholder='비밀번호' type='password' />
        </S.FormItem>
      </S.FormItemContainer>
      <S.SubmitButton>로그인하기</S.SubmitButton>
    </S.LoginForm>
  );
};

export default LoginForm;
