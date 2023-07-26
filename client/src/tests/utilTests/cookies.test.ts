import { deleteCookie, getCookie, setCookie } from '@utils/_common/cookies';

describe('cookie 관련 util function', () => {
  beforeEach(() => {
    document.cookie.split(';').forEach((c) => {
      document.cookie = c
        .replace(/^ +/, '')
        .replace(/=.*/, `=;expires=${new Date().toUTCString()};path=/`);
    });
  });

  test('쿠키를 생성 및 주입한다', () => {
    setCookie('test', 'value', 1);
    expect(document.cookie).toContain('test=value');
  });

  test('쿠키를 생성 및 주입한다 (만료일 없음)', () => {
    setCookie('test', 'value');
    expect(document.cookie).toContain('test=value');
  });

  test('쿠키를 확인한다', () => {
    setCookie('test', 'value', 1);
    const value = getCookie('test');
    expect(value).toEqual('value');
  });

  test('존재하지 않는 쿠키를 확인한다', () => {
    const value = getCookie('nonexistent');
    expect(value).toBeNull();
  });

  test('여러 쿠키 중에 일치하는 쿠키가 없는 경우를 확인한다', () => {
    setCookie('test1', 'value1', 1);
    setCookie('test2', 'value2', 1);
    setCookie('test3', 'value3', 1);

    const value = getCookie('nonexistent');
    expect(value).toBeNull();
  });

  test('쿠키를 삭제한다', () => {
    setCookie('test', 'value', 1);
    deleteCookie('test');
    expect(document.cookie).not.toContain('test=value');
  });
});
