import { createContext, PropsWithChildren, useContext } from 'react';
import { UserInfoResponse } from '@myTypes/user/remote';
import { UserInfoContextType } from '@myTypes/_common/user';
import { useIdentifyUser } from '@/hooks/_common/useIdentifyUser';

export const defaultUserInfo: UserInfoResponse = {
  id: null,
  nickname: '',
  profileImageUrl: '',
  gender: null,
  identifier: '',
  email: '',
};

export const userInfoContext = createContext<UserInfoContextType>({
  userInfo: defaultUserInfo,
  setUserInfo: () => {},
});

const UserInfoProvider = ({ children }: PropsWithChildren) => {
  const { userInfo, setUserInfo } = useIdentifyUser();

  return (
    <userInfoContext.Provider value={{ userInfo, setUserInfo }}>
      {children}
    </userInfoContext.Provider>
  );
};

export const useUserInfoContext = () => useContext(userInfoContext);

export default UserInfoProvider;
