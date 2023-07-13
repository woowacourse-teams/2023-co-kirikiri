export type UserResponse = {
  data: Data;
  support: Support;
};

export type Data = {
  id: number;
  email: string;
  first_name: string;
  last_name: string;
  avatar: string;
};

export type Support = {
  url: string;
  text: string;
};
