import formatDate from '@utils/_common/formatDate';

describe('날짜 형식 변환', () => {
  it('2023-08-04T14:25:03.469081을 2023-08-04 형식으로 변환해야 함', () => {
    const inputDate = '2023-08-04T14:25:03.469081';
    const expectedOutput = '2023-08-04';
    expect(formatDate(inputDate)).toBe(expectedOutput);
  });
});
