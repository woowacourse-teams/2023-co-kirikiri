import { UserInfoResponse } from '@myTypes/user/remote';

const isValidUserInfo = (userInfo: UserInfoResponse): boolean => {
  return Object.entries(userInfo).every(([key, value]) => {
    if (key === 'email') {
      return true;
    }
    return Boolean(value);
  });
};

export default isValidUserInfo;
