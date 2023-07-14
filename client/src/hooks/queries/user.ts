import { useQuery } from '@tanstack/react-query';
import getUser from '@apis/user';
import QUERY_KEYS from '@constants/@queryKeys/queryKeys';

export const useFetchUser = () => {
  const { data } = useQuery([QUERY_KEYS.user.user], getUser);

  return {
    user: data?.data,
  };
};
