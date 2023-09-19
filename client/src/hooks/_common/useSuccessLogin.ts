import { useNavigate } from 'react-router-dom';
import { AxiosResponse } from 'axios';
import useToast from './useToast';
import { getUserInfo } from '@apis/user';
import { useUserInfoContext } from '@components/_providers/UserInfoProvider';
import { UserInfoResponse, UserLoginResponse } from '@myTypes/user/remote';
import { setCookie } from '@utils/_common/cookies';

export const useSuccessLogin = () => {
  const navigate = useNavigate();
  const { triggerToast } = useToast();
  const { setUserInfo } = useUserInfoContext();

  const onSuccess = ({ accessToken, refreshToken }: UserLoginResponse) => {
    setCookie('access_token', accessToken);
    setCookie('refresh_token', refreshToken);
    triggerToast({ message: '로그인 성공!' });

    getUserInfo().then((response: AxiosResponse<UserInfoResponse>) => {
      setUserInfo(response.data);
    });

    navigate('/roadmap-list');
  };

  return { onSuccess };
};
