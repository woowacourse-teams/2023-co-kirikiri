type AnyObject = { [key: string]: any };

export const convertFieldsToNumber = (
  obj: AnyObject,
  keysToConvert: string[]
): AnyObject => {
  return Object.entries(obj).reduce((acc: AnyObject, [key, value]: [string, any]) => {
    // value가 string일 때
    if (keysToConvert.includes(key) && typeof value === 'string') {
      return { ...acc, [key]: Number(value) };
    }

    // value가 {} 형식일 때
    if (typeof value === 'object' && value !== null && !Array.isArray(value)) {
      return { ...acc, [key]: convertFieldsToNumber(value, keysToConvert) };
    }

    // value가 배열일 때
    if (Array.isArray(value)) {
      return {
        ...acc,
        [key]: value.map((item) => convertFieldsToNumber(item, keysToConvert)),
      };
    }

    return { ...acc, [key]: value };
  }, {});
};
