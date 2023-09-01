import { getInvariantObjectKeys, invariantOf } from '@utils/_common/invariantType';

describe('유틸 함수 테스트', () => {
  describe('invariantOf 함수', () => {
    test('입력값과 출력값이 동일한지 확인', () => {
      const input = { a: 1, b: 2 };
      const output = invariantOf(input);
      expect(output).toEqual(input);
    });
  });

  describe('getInvariantObjectKeys 함수', () => {
    test('객체의 키들이 배열로 반환되는지 확인', () => {
      const input = invariantOf({ a: 1, b: 2 });
      const output = getInvariantObjectKeys(input);
      expect(output).toEqual(['a', 'b']);
    });

    test('객체의 키 배열이 순서대로 반환되는지 확인', () => {
      const input = invariantOf({ b: 1, a: 2 });
      const output = getInvariantObjectKeys(input);
      expect(output).toEqual(['b', 'a']);
    });

    test('빈 객체가 들어올 경우 빈 배열을 반환하는지 확인', () => {
      const input = invariantOf({});
      const output = getInvariantObjectKeys(input);
      expect(output).toEqual([]);
    });
  });
});
