import client from '@apis/axios/client';
import type {
  MemberJoinRequest,
  UserLoginRequest,
  UserLoginResponse,
} from '@myTypes/user/remote';
import { UserInfoResponse } from '@myTypes/user/remote';

export const signUp = (body: MemberJoinRequest) => {
  return client.post('/members/join', body);
};

export const login = (body: UserLoginRequest) => {
  return client.post<UserLoginResponse>('/auth/login', body);
};

export const getUserInfo = () => {
  return client.get<UserInfoResponse>('/members/me');
};
