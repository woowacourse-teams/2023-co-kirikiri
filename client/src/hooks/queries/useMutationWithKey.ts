import { NETWORK_ERROR, TOAST_CONTENTS } from '@/constants/_common/toast';
import { MutationFunction, useMutation, UseMutationOptions } from '@tanstack/react-query';
import { AxiosError } from 'axios';
import useToast from '../_common/useToast';

export const useMutationWithKey = <
  TData = unknown,
  TError = unknown,
  TVariables = void,
  TContext = unknown
>(
  key: keyof typeof TOAST_CONTENTS,
  mutationFn: MutationFunction<TData, TVariables>,
  options?: Omit<UseMutationOptions<TData, TError, TVariables, TContext>, 'mutationFn'>
) => {
  const { triggerToast } = useToast();
  const { mutate, ...restProps } = useMutation<TData, TError, TVariables, TContext>(
    mutationFn,
    {
      ...options,

      onSuccess: (...args) => {
        triggerToast({
          message: TOAST_CONTENTS[key].success.message,
          indicator: TOAST_CONTENTS[key].success.indicator,
        });
        options?.onSuccess?.(...args);
      },

      onMutate: () => {
        if (!window.navigator.onLine) {
          triggerToast({
            message: NETWORK_ERROR.message,
            indicator: NETWORK_ERROR.indicator,
            isError: true,
          });
        }

        return undefined;
      },

      onError: (e, ...arg) => {
        // TODO : custom error을 통해 에러 분류

        if (e instanceof AxiosError && e.response?.status === 400) {
          triggerToast({
            message: e.response?.data[0].message,
            indicator: TOAST_CONTENTS[key].error.indicator,
            isError: true,
          });
        } else {
          triggerToast({
            message: TOAST_CONTENTS[key].error.message,
            indicator: TOAST_CONTENTS[key].error.indicator,
            isError: true,
          });
        }
        options?.onError?.(e, ...arg);
      },
    }
  );

  return {
    mutate,
    ...restProps,
  };
};
