import { client } from '@apis/axios/client';
import type { UserResponse } from '@myTypes/user/remote';

const getUser = async () => {
  const { data } = await client.get<UserResponse>('https://reqres.in/api/users/2');

  return data;
};

export default getUser;
