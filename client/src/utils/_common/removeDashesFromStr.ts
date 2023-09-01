/*
    문자열의 '-'를 제거하는 함수

    @param str: string
    @return string

    Example:
    removeDashesFromStr('hello-world') => 'helloworld'
 */

const removeDashesFromStr = (str: string): string => {
  return str.replace(/-/g, '');
};

export default removeDashesFromStr;
