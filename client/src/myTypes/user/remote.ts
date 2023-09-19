export type MemberJoinRequest = {
  identifier: string;
  password: string;
  email: string;
  nickname: string;
  genderType: string;
};

export type UserLoginRequest = {
  identifier: string;
  password: string;
};

export type UserLoginResponse = {
  accessToken: string;
  refreshToken: string;
};

export type NaverLoginRedirectResponse = {
  url: string;
  state: string;
};

export type OAuthResponse = UserLoginResponse;

export type UserGender = 'MALE' | 'FEMALE';

export type UserInfoResponse = {
  id: number | null;
  nickname: string;
  profileImageUrl: string;
  gender: UserGender | null;
  identifier: string;
};
