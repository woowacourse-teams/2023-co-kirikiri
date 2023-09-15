import client from '@apis/axios/client';
import {
  MemberJoinRequest,
  UserLoginRequest,
  UserLoginResponse,
  UserInfoResponse,
} from '@myTypes/user/remote';

export const signUp = (body: MemberJoinRequest) => {
  return client.post('/members/join', body);
};

export const getNaverLoginRedirectUrl = async () => {
  const { data } = await client.get('/auth/oauth/naver');

  return data;
};

export const naverOAuthToken = async (code: string, state: string) => {
  const { data } = await client.get('/auth/login/oauth', {
    params: {
      code,
      state,
    },
  });

  return data;
};

export const login = (body: UserLoginRequest) => {
  return client.post<UserLoginResponse>('/auth/login', body);
};

export const getUserInfo = (signal?: AbortSignal) => {
  return client.get<UserInfoResponse>('/members/me', {
    signal,
  });
};
