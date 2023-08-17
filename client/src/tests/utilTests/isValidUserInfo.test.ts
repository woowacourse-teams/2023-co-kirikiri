import isValidUserInfo from '@utils/user/isValidUserInfo';
import { UserInfoResponse } from '@myTypes/user/remote';

describe('사용자 정보 검증', () => {
  it('모든 필드가 유효한 경우', () => {
    const userInfo: UserInfoResponse = {
      id: 1,
      nickname: '테스트',
      profileImageUrl: 'http://example.com/profile.jpg',
      gender: 'MALE',
      identifier: 'test',
      phoneNumber: '010-1234-5678',
      birthday: '1990-01-01',
    };

    expect(isValidUserInfo(userInfo)).toBe(true);
  });

  it('필드가 누락된 경우', () => {
    const userInfo = {
      id: null,
      nickname: '',
      profileImageUrl: 'http://example.com/profile.jpg',
      gender: null,
      identifier: '',
      phoneNumber: '',
      birthday: '',
    };

    expect(isValidUserInfo(userInfo)).toBe(false);
  });
});
