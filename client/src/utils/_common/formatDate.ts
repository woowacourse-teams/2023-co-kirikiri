/*
    Timestamp 형식으로 돌아온 날짜를 yyyy-mm-dd 형식으로 변환

    @param {string} dateStr - Timestamp 형식의 날짜
    @returns {string} yyyy-mm-dd 형식의 날짜

    ex) formatDate('2021-01-01T00:00:00.000Z') => '2021-01-01'
 */

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0'); // Months are 0-indexed, so +1 is needed
  const day = String(date.getDate()).padStart(2, '0');

  return `${year}-${month}-${day}`;
};

export default formatDate;
