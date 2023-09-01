/*
    주어진 문자열 형식의 날짜가 오늘 || 이후인지 확인하는 함수 (이전만 아니면 됨)

    @param {string} date - 날짜 문자열
    @returns {boolean} - 오늘 || 이후인지 여부

    ex) isTodayOrAfter('2021-01-01') // true
 */

const isTodayOrAfter = (date: string) => {
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const dateToBeCompared = new Date(date);
  dateToBeCompared.setHours(0, 0, 0, 0);

  return dateToBeCompared >= today;
};

export default isTodayOrAfter;
