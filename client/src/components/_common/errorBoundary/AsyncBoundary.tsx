import { PropsWithChildren, Suspense } from 'react';
import { Critical, NotFound, Runtime, ServerError } from '../error/ErrorComponents';
import Spinner from '../spinner/Spinner';
import CriticalErrorBoundary from './CriticalErrorBoundary';
import NotFoundErrorBoundary from './NotFoundErrorBoundary';
import RuntimeErrorBoundary from './RuntimeErrorBoundary';
import ServerErrorBoundary from './ServerErrorBoundary';

const AsyncBoundary = ({ children }: PropsWithChildren) => {
  return (
    <CriticalErrorBoundary fallbackRender={() => <Critical />}>
      <RuntimeErrorBoundary fallbackRender={() => <Runtime />}>
        <ServerErrorBoundary fallbackRender={() => <ServerError />}>
          <NotFoundErrorBoundary fallbackRender={() => <NotFound />}>
            <Suspense fallback={<Spinner />}>{children}</Suspense>
          </NotFoundErrorBoundary>
        </ServerErrorBoundary>
      </RuntimeErrorBoundary>
    </CriticalErrorBoundary>
  );
};

export default AsyncBoundary;
