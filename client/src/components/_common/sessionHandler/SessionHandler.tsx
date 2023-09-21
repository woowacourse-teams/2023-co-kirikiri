import { PropsWithChildren, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import useToast from '@hooks/_common/useToast';
import {
  defaultUserInfo,
  useUserInfoContext,
} from '@components/_providers/UserInfoProvider';
import { deleteCookie } from '@utils/_common/cookies';

const SessionHandler = (props: PropsWithChildren) => {
  const navigate = useNavigate();
  const { triggerToast } = useToast();
  const { setUserInfo } = useUserInfoContext();

  useEffect(() => {
    const handleUnauthorized = () => {
      deleteCookie('access_token');
      deleteCookie('refresh_token');
      setUserInfo(defaultUserInfo);
      triggerToast({ message: '세션이 만료되었습니다. 재로그인 해주세요.' });
      navigate('/login');
    };

    window.addEventListener('unauthorized', handleUnauthorized);
    return () => {
      window.removeEventListener('unauthorized', handleUnauthorized);
    };
  }, []);

  return <>{props.children}</>;
};

export default SessionHandler;
