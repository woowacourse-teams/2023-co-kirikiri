type StateType<T> = {
  (params: T): void;
};

export type CombineStateType = {
  <T>(externalState?: StateType<T>, innerState?: StateType<T>): StateType<T>;
};
