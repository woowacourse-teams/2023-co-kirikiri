import { UserInfoResponse } from '@myTypes/user/remote';

const isValidUserInfo = (userInfo: UserInfoResponse): boolean => {
  return Object.values(userInfo).every((value) => Boolean(value));
};

export default isValidUserInfo;
