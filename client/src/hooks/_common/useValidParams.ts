import { useParams } from 'react-router-dom';

/*
 * useValidParams
 *
 * useParams를 사용할 때, undefined가 들어오는 경우가 있어 이를 방지하기 위해 사용하는 hook
 *
 * @returns {Record<string, string>}
 * @example
 *
 * const { id } = useValidParams<{ id: string }>();
 * console.log(id); // string
 *  */

const useValidParams = <T extends Record<string, string | undefined>>() => {
  const params = useParams<T>();
  if (!params || Object.values(params).some((value) => value === undefined)) {
    throw new Error('Invalid parameters');
  }

  return params as Record<string, string>;
};

export default useValidParams;
