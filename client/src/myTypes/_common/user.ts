import { defaultUserInfo } from '@components/_providers/UserInfoProvider';

export type UserInfoContextType = {
  userInfo: typeof defaultUserInfo;
  setUserInfo: React.Dispatch<React.SetStateAction<typeof defaultUserInfo>>;
};
