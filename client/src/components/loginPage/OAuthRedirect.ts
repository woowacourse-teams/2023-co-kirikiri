import { useNaverOAuth } from '@/hooks/queries/user';

const OAuthRedirect = () => {
  const urlParams = new URLSearchParams(window.location.search);
  const { code, state } = Object.entries(urlParams).reduce(
    (acc, [key, value]) => {
      if (key === 'error') throw Error();

      return { ...acc, [key]: value };
    },
    { code: '', state: '' }
  );

  useNaverOAuth(code, state);

  return null;
};

export default OAuthRedirect;
