import { isEmptyString } from '@utils/_common/isEmptyString';

describe('isEmptyString 함수 테스트', () => {
  it.each([
    ['', true],
    ['a', false],
    [' ', false],
    ['123', false],
    ['\t\n', false],
  ])('입력값 "%s"에 대한 반환값은 %s이어야 한다', (input, expected) => {
    expect(isEmptyString(input)).toBe(expected);
  });
});
