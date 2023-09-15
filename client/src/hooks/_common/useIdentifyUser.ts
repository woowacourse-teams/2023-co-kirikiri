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
};

export const useIdentifyUser = () => {
  const [userInfo, setUserInfo] = useState(defaultUserInfo);

  useEffect(() => {
    const fetchController = new AbortController();
    const { signal } = fetchController;

    const fetchUserInfo = async () => {
      try {
        const accessToken = getCookie('access_token');

        if (!accessToken) return;

        const userInfo = (await getUserInfo(signal)).data;

        setUserInfo(userInfo);
      } catch (e) {
        // 실패시 로직
      }
    };

    fetchUserInfo();

    return () => fetchController.abort();
  }, []);

  return {
    userInfo,
    setUserInfo,
  };
};
