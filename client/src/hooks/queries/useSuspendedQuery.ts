import {
  QueryFunction,
  QueryKey,
  useQuery,
  UseQueryOptions,
  UseQueryResult,
} from '@tanstack/react-query';
import { parseQueryArgs } from '@utils/_common/parseQueryArgs';

type UseQueryResultWithoutStatus = Omit<UseQueryResult, 'status'>;

interface BaseSuspendedUseQueryResult<TData> extends UseQueryResultWithoutStatus {
  data: TData;
  status: 'success' | 'idle';
}

export type SuspendedUseQueryResultOnSuccess<TData> =
  BaseSuspendedUseQueryResult<TData> & {
    status: 'success';
    isSuccess: true;
    isIdle: false;
  };
export type SuspendedUseQueryResultOnIdle = BaseSuspendedUseQueryResult<undefined> & {
  status: 'idle';
  isSuccess: false;
  isIdle: true;
};

export type SuspendedUseQueryOptions<
  TQueryFnData = unknown,
  TError = unknown,
  TData = TQueryFnData,
  TQueryKey extends QueryKey = QueryKey
> = Omit<UseQueryOptions<TQueryFnData, TError, TData, TQueryKey>, 'suspense'>;

// arg1: queryKey, arg2: queryFn, arg3: options
export function useSuspendedQuery<
  TQueryFnData = unknown,
  TError = unknown,
  TData = TQueryFnData,
  TQueryKey extends QueryKey = QueryKey
>(
  queryKey: TQueryKey,
  queryFn: QueryFunction<TQueryFnData, TQueryKey>,
  options?: Omit<
    SuspendedUseQueryOptions<TQueryFnData, TError, TData, TQueryKey>,
    'enabled' | 'queryKey' | 'queryFn'
  >
): SuspendedUseQueryResultOnSuccess<TData>;
export function useSuspendedQuery<
  TQueryFnData = unknown,
  TError = unknown,
  TData = TQueryFnData,
  TQueryKey extends QueryKey = QueryKey
>(
  queryKey: TQueryKey,
  queryFn: QueryFunction<TQueryFnData, TQueryKey>,
  options: Omit<
    SuspendedUseQueryOptions<TQueryFnData, TError, TData, TQueryKey>,
    'enabled' | 'queryKey' | 'queryFn'
  > & {
    enabled?: true;
  }
): SuspendedUseQueryResultOnSuccess<TData>;

export function useSuspendedQuery<
  TQueryFnData = unknown,
  TError = unknown,
  TData = TQueryFnData,
  TQueryKey extends QueryKey = QueryKey
>(
  queryKey: TQueryKey,
  queryFn: QueryFunction<TQueryFnData, TQueryKey>,
  options: Omit<
    SuspendedUseQueryOptions<TQueryFnData, TError, TData, TQueryKey>,
    'enabled' | 'queryKey' | 'queryFn'
  > & {
    enabled: false;
  }
): SuspendedUseQueryResultOnIdle;
export function useSuspendedQuery<
  TQueryFnData = unknown,
  TError = unknown,
  TData = TQueryFnData,
  TQueryKey extends QueryKey = QueryKey
>(
  queryKey: TQueryKey,
  queryFn: QueryFunction<TQueryFnData, TQueryKey>,
  options: Omit<
    SuspendedUseQueryOptions<TQueryFnData, TError, TData, TQueryKey>,
    'queryKey' | 'queryFn'
  >
): SuspendedUseQueryResultOnSuccess<TData> | SuspendedUseQueryResultOnIdle;

// arg1: queryKey, arg2: options
export function useSuspendedQuery<
  TQueryFnData = unknown,
  TError = unknown,
  TData = TQueryFnData,
  TQueryKey extends QueryKey = QueryKey
>(
  queryKey: TQueryKey,
  options?: Omit<
    SuspendedUseQueryOptions<TQueryFnData, TError, TData, TQueryKey>,
    'enabled' | 'queryKey'
  >
): SuspendedUseQueryResultOnSuccess<TData>;
export function useSuspendedQuery<
  TQueryFnData = unknown,
  TError = unknown,
  TData = TQueryFnData,
  TQueryKey extends QueryKey = QueryKey
>(
  queryKey: TQueryKey,
  options: Omit<
    SuspendedUseQueryOptions<TQueryFnData, TError, TData, TQueryKey>,
    'enabled' | 'queryKey'
  > & {
    enabled?: true;
  }
): SuspendedUseQueryResultOnSuccess<TData>;
export function useSuspendedQuery<
  TQueryFnData = unknown,
  TError = unknown,
  TData = TQueryFnData,
  TQueryKey extends QueryKey = QueryKey
>(
  queryKey: TQueryKey,
  options: Omit<
    SuspendedUseQueryOptions<TQueryFnData, TError, TData, TQueryKey>,
    'enabled' | 'queryKey'
  > & {
    enabled: false;
  }
): SuspendedUseQueryResultOnIdle;
export function useSuspendedQuery<
  TQueryFnData = unknown,
  TError = unknown,
  TData = TQueryFnData,
  TQueryKey extends QueryKey = QueryKey
>(
  queryKey: TQueryKey,
  options: Omit<
    SuspendedUseQueryOptions<TQueryFnData, TError, TData, TQueryKey>,
    'queryKey'
  >
): SuspendedUseQueryResultOnSuccess<TData> | SuspendedUseQueryResultOnIdle;

// arg1: options
export function useSuspendedQuery<
  TQueryFnData = unknown,
  TError = unknown,
  TData = TQueryFnData,
  TQueryKey extends QueryKey = QueryKey
>(
  options: Omit<
    SuspendedUseQueryOptions<TQueryFnData, TError, TData, TQueryKey>,
    'enabled'
  >
): SuspendedUseQueryResultOnSuccess<TData>;
export function useSuspendedQuery<
  TQueryFnData = unknown,
  TError = unknown,
  TData = TQueryFnData,
  TQueryKey extends QueryKey = QueryKey
>(
  options: Omit<
    SuspendedUseQueryOptions<TQueryFnData, TError, TData, TQueryKey>,
    'enabled'
  > & {
    enabled?: true;
  }
): SuspendedUseQueryResultOnSuccess<TData>;
export function useSuspendedQuery<
  TQueryFnData = unknown,
  TError = unknown,
  TData = TQueryFnData,
  TQueryKey extends QueryKey = QueryKey
>(
  options: Omit<
    SuspendedUseQueryOptions<TQueryFnData, TError, TData, TQueryKey>,
    'enabled'
  > & {
    enabled: false;
  }
): SuspendedUseQueryResultOnIdle;
export function useSuspendedQuery<
  TQueryFnData = unknown,
  TError = unknown,
  TData = TQueryFnData,
  TQueryKey extends QueryKey = QueryKey
>(
  options: SuspendedUseQueryOptions<TQueryFnData, TError, TData, TQueryKey>
): SuspendedUseQueryResultOnSuccess<TData> | SuspendedUseQueryResultOnIdle;

// base useSuspendedQuery
export function useSuspendedQuery<
  TQueryFnData = unknown,
  TError = unknown,
  TData = TQueryFnData,
  TQueryKey extends QueryKey = QueryKey
>(
  arg1: TQueryKey | SuspendedUseQueryOptions<TQueryFnData, TError, TData, TQueryKey>,
  arg2?:
    | QueryFunction<TQueryFnData, TQueryKey>
    | Omit<SuspendedUseQueryOptions<TQueryFnData, TError, TData, TQueryKey>, 'queryKey'>,
  arg3?: Omit<
    SuspendedUseQueryOptions<TQueryFnData, TError, TData, TQueryKey>,
    'queryKey' | 'queryFn'
  >
) {
  return useQuery({
    ...parseQueryArgs(arg1, arg2, arg3),
    suspense: true,
  }) as BaseSuspendedUseQueryResult<TData>;
}
