import { useUserInfoContext } from '@components/_providers/UserInfoProvider';
import { useEffect } from 'react';
import isValidUserInfo from '@utils/user/isValidUserInfo';
import logout from '@utils/user/logout';

export const useValidationCheck = () => {
  const { userInfo } = useUserInfoContext();

  useEffect(() => {
    if (!isValidUserInfo(userInfo)) {
      logout();
    }
  }, [userInfo]);
};

export default useValidationCheck;
