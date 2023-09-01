import { Context, useContext } from 'react';

export const useContextScope = <T>(context: Context<T>): T => {
  const contextValue = useContext(context);

  if (!context) throw new Error('Invalid Scope Context!');

  return contextValue;
};
