import { client } from '@apis/axios/client';
import type { UserResponse } from '@myTypes/user/remote';

const getUser = async () => {
  const { data } = await client.get<UserResponse>('/users/2');

  return data;
};

export default getUser;
