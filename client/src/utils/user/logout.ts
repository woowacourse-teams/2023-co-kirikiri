import { deleteCookie } from '@utils/_common/cookies';

const logout = () => {
  deleteCookie('access_token');
  deleteCookie('refresh_token');
};

export default logout;
