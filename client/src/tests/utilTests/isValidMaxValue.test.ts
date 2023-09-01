import { isValidMaxValue } from '@utils/_common/isValidMaxValue';

describe('isValidMaxValue 함수 테스트', () => {
  it.each([
    ['10', 10, true],
    ['10', 11, true],
    ['10', 9, false],
    ['-1', 0, true],
    ['10.5', 10.5, true],
    ['10.5', 10, false],
    ['10', 10.5, true],
  ])(
    '문자열 "%s"를 숫자로 변환한 값이 %d 이하인지 검사하면 결과는 %s이어야 한다',
    (inputValue, max, expected) => {
      expect(isValidMaxValue(inputValue, max)).toBe(expected);
    }
  );
});
