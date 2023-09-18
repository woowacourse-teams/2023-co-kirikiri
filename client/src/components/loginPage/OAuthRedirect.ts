import { useNaverOAuth } from '@hooks/queries/user';
import { useEffect } from 'react';

const OAuthRedirect = () => {
  const urlParams = new URLSearchParams(window.location.search);
  const { code, state, error } = Object.fromEntries(urlParams.entries());

  if (error) throw Error();

  const { naverLogin } = useNaverOAuth(code, state);

  useEffect(() => {
    naverLogin();
  }, []);

  return null;
};

export default OAuthRedirect;
