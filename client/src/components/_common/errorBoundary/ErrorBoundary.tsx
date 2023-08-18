import { Component, ReactNode } from 'react';
import ErrorBoundaryFallback from '@components/_common/errorBoundary/ErrorBoundaryFallback';

type MyError = {
  message: string;
};

type ErrorBoundaryProps = {
  children: ReactNode;
};

type ErrorBoundaryState = {
  hasError: boolean;
  error: MyError | null;
};

class ErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState> {
  constructor(props: ErrorBoundaryProps) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error: MyError) {
    return { hasError: true, error };
  }

  render() {
    const { hasError, error } = this.state;
    const { children } = this.props;

    if (hasError && error) {
      return <ErrorBoundaryFallback errorMessage={error.message} />;
    }

    return children;
  }
}

export default ErrorBoundary;
