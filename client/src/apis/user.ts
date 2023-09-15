import client from '@apis/axios/client';
import {
  MemberJoinRequest,
  OAuthResponse,
  UserLoginRequest,
  UserLoginResponse,
  UserInfoResponse,
} from '@myTypes/user/remote';

export const signUp = (body: MemberJoinRequest) => {
  return client.post('/members/join', body);
};

export const naverLogin = async () => {
  const {
    data: { url },
  } = await client.get('/auth/oauth/naver');
  const { data } = await client.get<OAuthResponse>(url);

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
