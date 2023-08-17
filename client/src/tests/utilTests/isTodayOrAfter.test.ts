import isTodayOrAfter from '@utils/_common/isTodayOrAfter';

describe('isTodayOrAfter 함수 테스트', () => {
  it('오늘 날짜에 대해서는 true를 반환해야 함', () => {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const todayString = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(
      2,
      '0'
    )}-${String(today.getDate()).padStart(2, '0')}`;
    expect(isTodayOrAfter(todayString)).toBe(true);
  });

  it('미래의 날짜에 대해서는 true를 반환해야 한다', () => {
    const futureDate = new Date();
    futureDate.setDate(futureDate.getDate() + 1);
    const futureDateString = futureDate.toISOString().split('T')[0];
    expect(isTodayOrAfter(futureDateString)).toBe(true);
  });

  it('과거의 날짜에 대해서는 false를 반환해야 한다', () => {
    const pastDate = new Date();
    pastDate.setDate(pastDate.getDate() - 1);
    const pastDateString = pastDate.toISOString().split('T')[0];
    expect(isTodayOrAfter(pastDateString)).toBe(false);
  });
});
