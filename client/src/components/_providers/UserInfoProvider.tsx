import { createContext, PropsWithChildren, useContext, useState } from 'react';
import { UserInfoResponse } from '@myTypes/user/remote';
import { UserInfoContextType } from '@myTypes/_common/user';

export const defaultUserInfo: UserInfoResponse = {
  id: null,
  nickname: '',
  profileImageUrl: '',
  gender: null,
  identifier: '',
  phoneNumber: '',
  birthday: '',
};

export const userInfoContext = createContext<UserInfoContextType>({
  userInfo: defaultUserInfo,
  setUserInfo: () => {},
});

const UserInfoProvider = ({ children }: PropsWithChildren) => {
  const [userInfo, setUserInfo] = useState<UserInfoResponse>(defaultUserInfo);

  return (
    <userInfoContext.Provider value={{ userInfo, setUserInfo }}>
      {children}
    </userInfoContext.Provider>
  );
};

export const useUserInfoContext = () => useContext(userInfoContext);

export default UserInfoProvider;
