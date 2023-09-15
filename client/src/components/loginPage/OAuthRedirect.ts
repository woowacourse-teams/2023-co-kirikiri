import { useNaverOAuth } from '@hooks/queries/user';

const OAuthRedirect = () => {
  const urlParams = new URLSearchParams(window.location.search);
  const { code, state, error } = Object.fromEntries(urlParams.entries());

  if (error) throw Error();

  useNaverOAuth(code, state);

  return null;
};

export default OAuthRedirect;
