import { transformDateStringsIn } from '@utils/_common/transformDateStringsIn';

describe('transformDateStringsIn 테스트', () => {
  it('객체 내부에 yyyy-mm-dd 형식의 string이 존재한다면 yyyymmdd 형식으로 변환한다', () => {
    const obj = {
      date: '2023-08-11',
    };

    const result = transformDateStringsIn(obj);

    expect(result.date).toBe('20230811');
  });

  it('중첩된 객체와 배열을 처리할 수 있어야 한다', () => {
    const obj = {
      nestedObj: {
        date: '2023-08-11',
      },
      nestedArray: [{ date: '2023-08-11' }, 'notADate', 456],
    };

    const result = transformDateStringsIn(obj);

    expect(result.nestedObj.date).toBe('20230811');
    expect(result.nestedArray[0]?.date).toBe('20230811');
    expect(result.nestedArray[1]).toBe('notADate');
    expect(result.nestedArray[2]).toBe(456);
  });

  it('깊게 중첩된 구조도 처리할 수 있어야 한다', () => {
    const obj = {
      deep: {
        deeper: {
          date: '2023-08-11',
          array: [{ evenDeeper: { date: '2023-08-11' } }],
        },
      },
    };

    const result = transformDateStringsIn(obj);

    expect(result.deep.deeper.date).toBe('20230811');
    expect(result.deep.deeper.array[0].evenDeeper.date).toBe('20230811');
  });

  it('빈 객체에 대해서는 빈 객체를 반환한다', () => {
    const obj = {};

    const result = transformDateStringsIn(obj);

    expect(result).toEqual({});
  });

  it('날짜가 아닌 문자열, 숫자, 불리언, null 등의 값은 수정되지 않아야 한다', () => {
    const obj = {
      str: 'hello',
      num: 123,
      bool: true,
      nil: null,
    };

    const result = transformDateStringsIn(obj);

    expect(result).toEqual(obj);
  });
});
