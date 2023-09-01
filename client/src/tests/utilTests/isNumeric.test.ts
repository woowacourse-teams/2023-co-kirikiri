import { isNumeric } from '@utils/_common/isNumeric';

describe('isNumeric 함수 테스트', () => {
  test.each([
    ['123', true],
    ['45678', true],
  ])('숫자로만 구성된 문자열을 올바르게 인식한다: %s', (input, expected) => {
    expect(isNumeric(input)).toBe(expected);
  });

  test.each([['0123', false]])(
    '0으로 시작하는 숫자는 올바른 숫자로 인식하지 않는다: %s',
    (input, expected) => {
      expect(isNumeric(input)).toBe(expected);
    }
  );

  test.each([
    ['123a', false],
    ['12.3', false],
    ['123-', false],
  ])(
    '문자나 특수문자가 포함된 문자열은 올바른 숫자로 인식하지 않는다: %s',
    (input, expected) => {
      expect(isNumeric(input)).toBe(expected);
    }
  );

  test.each([['', false]])(
    '빈 문자열은 올바른 숫자로 인식하지 않는다: %s',
    (input, expected) => {
      expect(isNumeric(input)).toBe(expected);
    }
  );
});
