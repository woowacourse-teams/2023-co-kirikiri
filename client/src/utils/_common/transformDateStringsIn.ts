import removeDashesFromStr from './removeDashesFromStr';

export type RecursiveObjectType = {
  // 객체 key의 value로 무엇이든 올 수 있기 때문에 any 지정
  [key: string]: any;
};

export const transformDateStringsIn = (obj: RecursiveObjectType): RecursiveObjectType => {
  if (typeof obj !== 'object' || obj == null) return obj;

  return Object.entries(obj).reduce((acc, [key, value]) => {
    if (typeof value === 'string' && /^(\d{4}-\d{2}-\d{2})$/.test(value)) {
      acc[key] = removeDashesFromStr(value);
    } else if (Array.isArray(value)) {
      acc[key] = value.map((item) => transformDateStringsIn(item as RecursiveObjectType));
    } else if (typeof value === 'object') {
      acc[key] = transformDateStringsIn(value as RecursiveObjectType);
    } else {
      acc[key] = value;
    }
    return acc;
  }, {} as RecursiveObjectType);
};
