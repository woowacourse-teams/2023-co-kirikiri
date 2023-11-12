import { ErrorBoundarySharedProps } from '@/myTypes/_common/errorBoundary';
import { PropsWithChildren, Suspense } from 'react';
import Spinner from '../spinner/Spinner';
import { ApiErrorBoundary } from './ApiErrorBoundary';
import { BaseErrorBoundary } from './BaseErrorBoundary';
import { APIError } from './errors';
import { NetworkErrorBoundary } from './NetworkErrorBoundary';
import RuntimeErrorBoundary from './RuntimeErrorBoundary';

interface AsyncBoundaryProps {
  isCritical?: boolean;
  onError?: ErrorBoundarySharedProps['onError'];
  onReset?: ErrorBoundarySharedProps['onReset'];
  resetKeys?: unknown[];
}

const AsyncBoundary = ({
  children,
  isCritical,
  onError,
  onReset,
  resetKeys,
}: PropsWithChildren<AsyncBoundaryProps>) => {
  return (
    <RuntimeErrorBoundary>
      <ApiErrorBoundary<APIError>>
        <NetworkErrorBoundary>
          <BaseErrorBoundary
            isCritical={isCritical}
            onError={onError}
            onReset={onReset}
            resetKeys={resetKeys}
          >
            <Suspense fallback={<Spinner />}>{children}</Suspense>
          </BaseErrorBoundary>
        </NetworkErrorBoundary>
      </ApiErrorBoundary>
    </RuntimeErrorBoundary>
  );
};

export default AsyncBoundary;
