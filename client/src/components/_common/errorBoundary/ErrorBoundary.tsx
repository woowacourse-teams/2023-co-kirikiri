import { ErrorBoundaryProps, ErrorBoundaryState } from '@/myTypes/_common/errorBoundary';
import { Component, PropsWithChildren } from 'react';

function hasArrayChanged(a: any[] = [], b: any[] = []) {
  return a.length !== b.length || a.some((item, index) => !Object.is(item, b[index]));
}

const initialState: ErrorBoundaryState = {
  didCatch: false,
  error: null,
};

class ErrorBoundary extends Component<
  PropsWithChildren<ErrorBoundaryProps>,
  ErrorBoundaryState
> {
  constructor(props: PropsWithChildren<ErrorBoundaryProps>) {
    super(props);

    this.state = initialState;
  }

  static getDerivedStateFromError(error: Error) {
    return { didCatch: true, error };
  }

  componentDidUpdate(prevProps: ErrorBoundaryProps, prevState: ErrorBoundaryState) {
    const { didCatch } = this.state;
    const { resetKeys } = this.props;

    if (
      didCatch &&
      prevState.error !== null &&
      hasArrayChanged(prevProps.resetKeys, resetKeys)
    ) {
      this.props.onReset?.({
        next: resetKeys,
        prev: prevProps.resetKeys,
        reason: 'keys',
      });

      this.setState(initialState);
    }
  }

  resetErrorBoundary(...args: unknown[]) {
    const { error } = this.state;

    if (error !== null) {
      this.props.onReset?.({
        args,
        reason: 'keys',
      });

      this.setState(initialState);
    }
  }
}

export default ErrorBoundary;
