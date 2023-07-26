import { deleteCookie } from '@utils/_common/cookies';
import logout from '@utils/user/logout';

jest.mock('@utils/_common/cookies', () => ({
  deleteCookie: jest.fn(),
}));

describe('logout 관련 util function', () => {
  it('access_token and refresh_token cookies 를 삭제한다', () => {
    logout();

    expect(deleteCookie).toHaveBeenCalledWith('access_token');
    expect(deleteCookie).toHaveBeenCalledWith('refresh_token');
  });
});
