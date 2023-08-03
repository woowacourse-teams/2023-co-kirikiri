import removeDashesFromStr from '@utils/_common/removeDashesFromStr';

describe('removeDashes function', () => {
  it('string 으로부터 모든 대시를 삭제한다.', () => {
    const input = '123-456-7890';
    const output = removeDashesFromStr(input);
    expect(output).toBe('1234567890');
  });

  it('문자열에 대시가 없다면 그대로 반환한다.', () => {
    const input = '1234567890';
    const output = removeDashesFromStr(input);
    expect(output).toBe('1234567890');
  });
});
