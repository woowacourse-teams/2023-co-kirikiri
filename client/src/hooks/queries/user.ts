import { useQuery } from '@tanstack/react-query';
import getUser from '@apis/user';

const QUERY_KEY = {
  user: 'user',
} as const;

export const useFetchUser = () => {
  const { data } = useQuery([QUERY_KEY.user], getUser);

  return {
    user: data?.data,
  };
};
