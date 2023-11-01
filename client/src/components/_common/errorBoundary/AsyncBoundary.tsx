import { PropsWithChildren, Suspense } from 'react';
import Spinner from '../spinner/Spinner';
import { ApiErrorBoundary } from './ApiErrorBoundary';
import { BaseErrorBoundary } from './BaseErrorBoundary';
import { APIError } from './errors';
import { NetworkErrorBoundary } from './NetworkErrorBoundary';
import RuntimeErrorBoundary from './RuntimeErrorBoundary';

interface AsyncBoundaryProps {
  isCritical?: boolean;
}

const AsyncBoundary = ({
  children,
  isCritical,
}: PropsWithChildren<AsyncBoundaryProps>) => {
  return (
    <RuntimeErrorBoundary>
      <ApiErrorBoundary<APIError>>
        <NetworkErrorBoundary>
          <BaseErrorBoundary isCritical={isCritical}>
            <Suspense fallback={<Spinner />}>{children}</Suspense>
          </BaseErrorBoundary>
        </NetworkErrorBoundary>
      </ApiErrorBoundary>
    </RuntimeErrorBoundary>
  );
};

export default AsyncBoundary;
