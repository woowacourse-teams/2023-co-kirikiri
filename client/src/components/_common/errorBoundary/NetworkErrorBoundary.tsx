import { ErrorInfo } from 'react';
import { Offline } from '../fallback/Fallback';
import ErrorBoundary from './ErrorBoundary';
import { NetworkError } from './errors';

export class NetworkErrorBoundary extends ErrorBoundary {
  static getDerivedStateFromError(error: Error): { didCatch: boolean; error: Error } {
    if (error instanceof NetworkError) {
      return { didCatch: true, error };
    }
    throw error;
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo): void {
    if (this.props.isCritical) throw error;

    this.props.onError?.(error, errorInfo);
    this.setState({ fallback: <Offline /> });
  }

  render() {
    const { didCatch, fallback: innerFallback } = this.state;
    const { children, fallback: customFllback } = this.props;
    if (didCatch) {
      return customFllback ?? innerFallback ?? <div>network error!</div>;
    }
    return children;
  }
}
