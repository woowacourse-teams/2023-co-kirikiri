export const isCurrentOrFutureDate = (dateStr: string) => {
  const inputDate = new Date(dateStr);
  const today = new Date();

  // 시간, 분, 초, 밀리초를 무시하고 날짜만 비교하기 위해 설정
  inputDate.setHours(0, 0, 0, 0);
  today.setHours(0, 0, 0, 0);

  return inputDate.getTime() >= today.getTime();
};
