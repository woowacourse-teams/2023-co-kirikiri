import { useMutation } from '@tanstack/react-query';
import {
  MemberJoinRequest,
  UserInfoResponse,
  UserLoginRequest,
} from '@myTypes/user/remote';
import {
  getNaverLoginRedirectUrl,
  getUserInfo,
  login,
  naverOAuthToken,
  signUp,
} from '@apis/user';
import useToast from '@hooks/_common/useToast';
import {
  defaultUserInfo,
  useUserInfoContext,
} from '@/components/_providers/UserInfoProvider';
import { AxiosResponse } from 'axios';
import { useNavigate } from 'react-router-dom';
import { setCookie } from '@/utils/_common/cookies';
import logout from '@utils/user/logout';
import { useMutationWithKey } from './useMutationWithKey';
import { TOAST_CONTENTS } from '@/constants/_common/toast';

export const useSignUp = () => {
  const navigate = useNavigate();

  const { mutate } = useMutationWithKey(
    'SIGN_UP',
    (memberJoinPayload: MemberJoinRequest) => signUp(memberJoinPayload),
    {
      onSuccess() {
        navigate('/login');
      },
    }
  );

  return {
    signUp: mutate,
  };
};

export const useNaverLogin = () => {
  // get 요청임에도 사용자와의 상호작용에 의해 명시적으로 제어해야 하기 때문에 mutation을 사용
  const { mutate } = useMutation(() => getNaverLoginRedirectUrl(), {
    onSuccess({ url }) {
      window.location.href = url;
    },
  });

  return { redirectToNaverLoginPage: mutate };
};

export const useNaverOAuth = (code: string, state: string) => {
  const navigate = useNavigate();
  const { setUserInfo } = useUserInfoContext();

  const { mutate } = useMutationWithKey('LOGIN', () => naverOAuthToken(code, state), {
    onSuccess: ({ accessToken, refreshToken }) => {
      setCookie('access_token', accessToken);
      setCookie('refresh_token', refreshToken);

      getUserInfo().then((response: AxiosResponse<UserInfoResponse>) => {
        setUserInfo(response.data);
      });

      navigate('/roadmap-list');
    },
  });

  return { naverLogin: mutate };
};

export const useLogin = () => {
  const navigate = useNavigate();
  const { setUserInfo } = useUserInfoContext();

  const { mutate } = useMutationWithKey(
    'LOGIN',
    (loginPayload: UserLoginRequest) => login(loginPayload),
    {
      onSuccess(response) {
        const { accessToken, refreshToken } = response.data;
        setCookie('access_token', accessToken);
        setCookie('refresh_token', refreshToken);

        getUserInfo().then((response: AxiosResponse<UserInfoResponse>) => {
          setUserInfo(response.data);
        });

        navigate('/roadmap-list');
      },
    }
  );

  return {
    login: mutate,
  };
};

export const useLogout = () => {
  const { triggerToast } = useToast();
  const { setUserInfo } = useUserInfoContext();

  const triggerLogout = () => {
    logout();
    setUserInfo(defaultUserInfo);
    triggerToast({
      message: TOAST_CONTENTS.LOGOUT.success.message,
      indicator: TOAST_CONTENTS.LOGOUT.success.indicator,
    });
  };

  return { logout: triggerLogout };
};
