import { isValidMaxLength } from '@utils/_common/isValidMaxLength';

describe('isValidMaxLength 함수 테스트', () => {
  it.each([
    ['test', 4, true],
    ['test', 5, true],
    ['test', 3, false],
    ['', 0, true],
    ['', 1, true],
    ['hello world', 11, true],
    ['hello world', 10, false],
  ])(
    '문자열 "%s"의 길이가 %d 이하인지 검사하면 결과는 %s이어야 한다',
    (input, maxLength, expected) => {
      expect(isValidMaxLength(input, maxLength)).toBe(expected);
    }
  );
});
