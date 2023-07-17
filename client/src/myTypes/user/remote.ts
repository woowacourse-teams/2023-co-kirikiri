export type MemberJoinRequest = {
  identifier: string;
  password: string;
  nickname: string;
  phoneNumber: string;
  genderType: string;
  birthDate: string;
};

export type UserLoginRequest = {
  identifier: string;
  password: string;
};

export type UserLoginResponse = {
  accessToken: string;
  refreshToken: string;
};
