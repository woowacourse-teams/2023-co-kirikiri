import { useMutation } from '@tanstack/react-query';
import {
  MemberJoinRequest,
  UserInfoResponse,
  UserLoginRequest,
} from '@myTypes/user/remote';
import { getUserInfo, login, signUp } from '@apis/user';
import useToast from '@hooks/_common/useToast';
import { useUserInfoContext } from '@/components/_providers/UserInfoProvider';
import { AxiosResponse } from 'axios';
import { setCookie } from '@/utils/_common/cookies';

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
  const { triggerToast } = useToast();
  const { setUserInfo } = useUserInfoContext();

  const { mutate } = useMutation(
    (loginPayload: UserLoginRequest) => login(loginPayload),
    {
      onSuccess(response) {
        const { accessToken, refreshToken } = response.data;
        setCookie('access_token', accessToken);
        setCookie('refresh_token', refreshToken);
        triggerToast({ message: '로그인 성공!' });

        getUserInfo().then((response: AxiosResponse<UserInfoResponse>) => {
          setUserInfo(response.data);
        });
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
