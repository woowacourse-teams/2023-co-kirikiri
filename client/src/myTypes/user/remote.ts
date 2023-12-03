export type MemberJoinRequest = {
  identifier: string;
  password: string;
  nickname: string;
  phoneNumber: string;
  genderType: string;
  birthday: string;
};

export type UserLoginRequest = {
  identifier: string;
  password: string;
};

export type OAuthResponse = {
  accessToken: string;
  refreshToken: string;
};

export type UserLoginResponse = {
  accessToken: string;
  refreshToken: string;
};

export type UserGender = 'MALE' | 'FEMALE';

export type UserInfoResponse = {
  id: number | null;
  nickname: string;
  profileImageUrl: string;
  gender: UserGender | null;
  identifier: string;
  phoneNumber: string;
  birthday: string;
};
