import { convertFieldsToNumber } from '@utils/_common/convertFieldsToNumber';

describe('convertFieldsToNumber 테스트', () => {
  it('객체 속성이 이름이 keysToConvert에 포함되어 있고, 값이 string형식이라면 값을 Number형으로 변환한다', () => {
    const anyObject = {
      a: '1',
      b: '2',
      c: '3',
    };

    const keysToConvert = ['a', 'b', 'c'];
    const expectedOutput = {
      a: 1,
      b: 2,
      c: 3,
    };

    expect(convertFieldsToNumber(anyObject, keysToConvert)).toEqual(expectedOutput);
  });

  it('객체의 key가 객체라면 value 객체의 key값으로도 동일하게 체크 후, 조건에 따라 형변환을 진행한다', () => {
    const anyObject = {
      a: '1',
      b: {
        c: '2',
        d: '3',
      },
    };

    const keysToConvert = ['a', 'c', 'd'];
    const expectedOutput = {
      a: 1,
      b: {
        c: 2,
        d: 3,
      },
    };

    expect(convertFieldsToNumber(anyObject, keysToConvert)).toEqual(expectedOutput);
  });

  it('객체의 key가 배열이라면 배열 내의 객체 역시 동일하게 체크 후, 조건에 따라 형변환을 진행한다', () => {
    const anyObject = {
      a: '1',
      b: [
        {
          c: '2',
          d: '3',
        },
        {
          e: '4',
          f: '5',
        },
      ],
    };

    const keysToConvert = ['a', 'c', 'd', 'e', 'f'];
    const expectedOutput = {
      a: 1,
      b: [
        {
          c: 2,
          d: 3,
        },
        {
          e: 4,
          f: 5,
        },
      ],
    };

    expect(convertFieldsToNumber(anyObject, keysToConvert)).toEqual(expectedOutput);
  });

  it('객체의 key 값이 keysToConvert배열에 포함되어 있지 않다면 그래도 string 형을 유지한다', () => {
    const input = {
      a: '1',
      b: '2',
      c: '3',
    };

    const keysToConvert = ['a', 'c'];
    const expectedOutput = {
      a: 1,
      b: '2',
      c: 3,
    };

    expect(convertFieldsToNumber(input, keysToConvert)).toEqual(expectedOutput);
  });
});
