import { useMutation } from '@tanstack/react-query';
import { MemberJoinRequest, UserLoginRequest } from '@myTypes/user/remote';
import { getNaverLoginRedirectUrl, login, naverOAuthToken, signUp } from '@apis/user';
import useToast from '@hooks/_common/useToast';
import {
  defaultUserInfo,
  useUserInfoContext,
} from '@/components/_providers/UserInfoProvider';
import { useNavigate } from 'react-router-dom';
import logout from '@utils/user/logout';
import { useSuccessLogin } from '../_common/useSuccessLogin';

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
  const { onSuccess } = useSuccessLogin();

  const { mutate } = useMutation(() => naverOAuthToken(code, state), {
    onSuccess,
  });

  return { naverLogin: mutate };
};

export const useLogin = () => {
  const { onSuccess } = useSuccessLogin();

  const { mutate } = useMutation(
    async (loginPayload: UserLoginRequest) => {
      const response = await login(loginPayload);
      return response.data;
    },
    {
      onSuccess,
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
