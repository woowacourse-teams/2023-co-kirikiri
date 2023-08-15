import { isCurrentOrFutureDate } from '@utils/_common/isCurrentOrFutureDate';

it('과거의 날짜를 입력하면 false를 반환한다', () => {
  const isValidateDate = isCurrentOrFutureDate('2022-03-14');

  expect(isValidateDate).toBe(false);
});
