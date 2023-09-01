import { parseQueryArgs } from '@utils/_common/parseQueryArgs';

describe('parseQueryArgs 함수 테스트', () => {
  it('arg1이 쿼리 키가 아닌 경우 테스트', () => {
    const options = { queryKey: ['test'], queryFn: () => {} };
    const result = parseQueryArgs(options);
    expect(result).toBe(options);
  });

  it('arg2가 함수인 경우 테스트', () => {
    const key = ['test'];
    const fn = () => {};
    const options = { retry: false };
    const result = parseQueryArgs(key, fn, options);
    expect(result).toEqual({ ...options, queryKey: key, queryFn: fn });
  });

  it('arg2가 함수가 아닌 경우 테스트', () => {
    const key = ['test'];
    const options = { queryKey: ['test2'], queryFn: () => {} };
    const result = parseQueryArgs(key, options);
    expect(result).toEqual({ ...options, queryKey: key });
  });
});
