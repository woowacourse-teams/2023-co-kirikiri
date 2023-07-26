import { isQueryKey } from '@utils/_common/isQueryKey';

describe('isQueryKey 함수 테스트', () => {
  it('문자열에 대한 테스트', () => {
    const result = isQueryKey('test');
    expect(result).toBe(true);
  });

  it('배열에 대한 테스트', () => {
    const result = isQueryKey(['test', 'test2']);
    expect(result).toBe(true);
  });

  it('문자열이나 배열이 아닌 경우에 대한 테스트', () => {
    const result = isQueryKey({ key: 'test' });
    expect(result).toBe(false);
  });
});
