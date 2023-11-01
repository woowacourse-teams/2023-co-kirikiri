import { ErrorInfo } from 'react';
import { Runtime } from '../error/ErrorComponents';
import ErrorBoundary from './ErrorBoundary';

class RuntimeErrorBoundary extends ErrorBoundary {
  static getDerivedStateFromError(error: Error): { didCatch: boolean; error: Error } {
    return { didCatch: true, error };
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo): void {
    if (this.props.isCritical) throw error;

    this.props.onError?.(error, errorInfo);
  }

  render() {
    const { didCatch } = this.state;
    const { children, fallback: customFallback } = this.props;

    if (didCatch) {
      return customFallback ?? <Runtime />;
    }
    return children;
  }
}

export default RuntimeErrorBoundary;
