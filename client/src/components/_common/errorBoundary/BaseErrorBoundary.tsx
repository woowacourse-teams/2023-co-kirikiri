import { CkError } from './errors';
import ErrorBoundary from './ErrorBoundary';
import { ErrorInfo } from 'react';

export class BaseErrorBoundary extends ErrorBoundary {
  componentDidCatch(error: Error, errorInfo: ErrorInfo): void {
    this.props.onError?.(error, errorInfo);

    throw CkError.convertError(error);
  }

  render() {
    const { children } = this.props;
    const { didCatch } = this.state;

    if (didCatch) {
      return null;
    }

    return children;
  }
}
