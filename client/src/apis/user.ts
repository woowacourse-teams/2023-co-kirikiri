import { API_PATH } from '@/constants/_common/api';
import client from '@apis/axios/client';
import {
  MemberJoinRequest,
  UserLoginRequest,
  UserLoginResponse,
  UserInfoResponse,
  NaverLoginRedirectResponse,
  OAuthResponse,
} from '@myTypes/user/remote';

export const signUp = (body: MemberJoinRequest) => {
  return client.post(API_PATH.SIGN_UP, body);
};

export const getNaverLoginRedirectUrl = async () => {
  const { data } = await client.get<NaverLoginRedirectResponse>(
    API_PATH.NAVER_LOGIN_REDIRECT
  );

  return data;
};

export const naverOAuthToken = async (code: string, state: string) => {
  const { data } = await client.get<OAuthResponse>(API_PATH.NAVER_TOKEN, {
    params: {
      code,
      state,
    },
  });

  return data;
};

export const login = (body: UserLoginRequest) => {
  return client.post<UserLoginResponse>(API_PATH.LOGIN, body);
};

export const getUserInfo = (signal?: AbortSignal) => {
  return client.get<UserInfoResponse>(API_PATH.USER_INFO, {
    signal,
  });
};
