import { useMutation, useQuery } from '@tanstack/react-query';
import {
  MemberJoinRequest,
  UserInfoResponse,
  UserLoginRequest,
} from '@myTypes/user/remote';
import { getUserInfo, login, naverLogin, naverOAuthToken, signUp } from '@apis/user';
import useToast from '@hooks/_common/useToast';
import {
  defaultUserInfo,
  useUserInfoContext,
} from '@/components/_providers/UserInfoProvider';
import { AxiosResponse } from 'axios';
import { useNavigate } from 'react-router-dom';
import { setCookie } from '@/utils/_common/cookies';
import logout from '@utils/user/logout';
import { useSuspendedQuery } from './useSuspendedQuery';
import QUERY_KEYS from '@/constants/@queryKeys/queryKeys';

export const useSignUp = () => {
  const { triggerToast } = useToast();
  const navigate = useNavigate();

  const { mutate } = useMutation(
    (memberJoinPayload: MemberJoinRequest) => signUp(memberJoinPayload),
    {
      onSuccess() {
        triggerToast({ message: '회원가입 성공!' });
        navigate('/login');
      },
    }
  );

  return {
    signUp: mutate,
  };
};

export const useNaverLogin = async () => {
  useQuery([QUERY_KEYS.user.naver_oauth], () => naverLogin());
};

export const useNaverOAuth = async (code: string, state: string) => {
  const navigate = useNavigate();
  const { triggerToast } = useToast();
  const { setUserInfo } = useUserInfoContext();

  useSuspendedQuery(['naver_oauth_token'], async () => {
    const { accessToken, refreshToken } = await naverOAuthToken(code, state);

    setCookie('access_token', accessToken);
    setCookie('refresh_token', refreshToken);
    triggerToast({ message: '로그인 성공!' });
    getUserInfo().then((response: AxiosResponse<UserInfoResponse>) => {
      setUserInfo(response.data);
    });
    navigate('/roadmap-list');
  });
};

export const useLogin = () => {
  const navigate = useNavigate();
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
    triggerToast({ message: '로그아웃 성공!' });
  };

  return { logout: triggerLogout };
};
