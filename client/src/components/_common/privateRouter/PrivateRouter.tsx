import { PropsWithChildren, useEffect } from 'react';
import { useUserInfoContext } from '@components/_providers/UserInfoProvider';
import useToast from '@hooks/_common/useToast';
import { useNavigate } from 'react-router-dom';
import { getCookie } from '@utils/_common/cookies';
import SVGIcon from '@/components/icons/SVGIcon';

const PrivateRouter = (props: PropsWithChildren) => {
  const { children } = props;
  const { userInfo } = useUserInfoContext();
  const { triggerToast } = useToast();
  const navigate = useNavigate();
  const accessToken = getCookie('access_token');

  useEffect(() => {
    if (userInfo.id === null && !accessToken) {
      navigate('/login');
      triggerToast({
        message: '로그인이 필요한 서비스입니다.',
        indicator: <SVGIcon name='ErrorIcon' />,
        isError: true,
      });
    }
  }, [userInfo.id, navigate]);

  return <>{children}</>;
};

export default PrivateRouter;
