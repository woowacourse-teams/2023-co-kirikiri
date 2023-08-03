import { useMutation } from '@tanstack/react-query';
import { MemberJoinRequest, UserLoginRequest } from '@myTypes/user/remote';
import { login, signUp } from '@apis/user';
import { setCookie } from '@utils/_common/cookies';

export const useSignUp = () => {
  const { mutate } = useMutation(
    (memberJoinPayload: MemberJoinRequest) => signUp(memberJoinPayload),
    {
      onSuccess() {
        // TODO: 회원가입 성공 시 로직
      },
      onError() {
        // TODO: 회원가입 실패 시 로직
      },
    }
  );

  return {
    signUp: mutate,
  };
};

export const useLogin = () => {
  const { mutate } = useMutation(
    (loginPayload: UserLoginRequest) => login(loginPayload),
    {
      onSuccess(response) {
        const { accessToken, refreshToken } = response.data;
        setCookie('access_token', accessToken);
        setCookie('refresh_token', refreshToken);
      },
      onError() {
        // TODO: 로그인 실패 시 로직
      },
    }
  );

  return {
    login: mutate,
  };
};
