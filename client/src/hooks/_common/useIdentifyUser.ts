import { getUserInfo } from '@/apis/user';
import { UserInfoResponse } from '@/myTypes/user/remote';
import { getCookie } from '@/utils/_common/cookies';
import { useEffect, useState } from 'react';

export const defaultUserInfo: UserInfoResponse = {
  id: null,
  nickname: '',
  profileImageUrl: '',
  gender: null,
  identifier: '',
  phoneNumber: '',
  birthday: '',
};

export const useIdentifyUser = () => {
  const [userInfo, setUserInfo] = useState(defaultUserInfo);

  useEffect(() => {
    const accessToken = getCookie('access_token');
    if (!accessToken) return;

    getUserInfo().then((response) => setUserInfo(response.data));
  }, []);

  return {
    userInfo,
    setUserInfo,
  };
};
