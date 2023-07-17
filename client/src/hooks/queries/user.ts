import { useMutation } from '@tanstack/react-query';
import { MemberJoinRequest, UserLoginRequest } from '@myTypes/user/remote';
import { login, signUp } from '@apis/user';
import { setCookie } from '@utils/_common/cookies';

export const useSignUp = (memberJoinPayload: MemberJoinRequest) => {
  const { mutate } = useMutation(() => signUp(memberJoinPayload), {
    onSuccess() {
      // TODO: 회원가입 성공 시 로직
    },
    onError() {
      // TODO: 회원가입 실패 시 로직
    },
  });

  return {
    signUp: mutate,
  };
};

export const useLogin = (loginPayload: UserLoginRequest) => {
  return useMutation(() => login(loginPayload), {
    onSuccess(response) {
      const { accessToken, refreshToken } = response.data;
      setCookie('access_token', accessToken);
      setCookie('refresh_token', refreshToken);
      alert(`로그인 성공!  ${accessToken}`);
    },
    onError() {
      // TODO: 로그인 실패 시 로직
    },
  });
};
