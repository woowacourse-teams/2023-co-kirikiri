import { ErrorInfo } from 'react';
import { Critical } from '../fallback/Fallback';
import ErrorBoundary from './ErrorBoundary';

export class CriticalErrorBoundary extends ErrorBoundary {
  static getDerivedStateFromError(error: Error): { didCatch: boolean; error: Error } {
    return { didCatch: true, error };
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo): void {
    this.props.onError?.(error, errorInfo);
    this.setState({ fallback: <Critical /> });
  }

  render() {
    const { didCatch, fallback: innerFallback } = this.state;
    const { children, fallback: custumFallback } = this.props;

    if (didCatch) {
      return custumFallback ?? innerFallback ?? <div>critical error</div>;
    }
    return children;
  }
}
