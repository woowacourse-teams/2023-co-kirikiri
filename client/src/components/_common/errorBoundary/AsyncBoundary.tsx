import { PropsWithChildren, Suspense } from 'react';
import Spinner from '../spinner/Spinner';
import CriticalErrorBoundary from './CriticalErrorBoundary';
import NotFoundErrorBoundary from './NotFoundErrorBoundary';
import RuntimeErrorBoundary from './RuntimeErrorBoundary';
import ServerErrorBoundary from './ServerErrorBoundary';

const AsyncBoundary = ({ children }: PropsWithChildren) => {
  return (
    <CriticalErrorBoundary>
      <RuntimeErrorBoundary>
        <ServerErrorBoundary>
          <NotFoundErrorBoundary>
            <Suspense fallback={<Spinner />}>{children}</Suspense>
          </NotFoundErrorBoundary>
        </ServerErrorBoundary>
      </RuntimeErrorBoundary>
    </CriticalErrorBoundary>
  );
};

export default AsyncBoundary;
