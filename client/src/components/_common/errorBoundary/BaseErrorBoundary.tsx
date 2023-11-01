import { CkError } from './errors';
import ErrorBoundary from './ErrorBoundary';

export class BaseErrorBoundary extends ErrorBoundary {
  static getDerivedStateFromError(error: Error): { didCatch: boolean; error: Error } {
    throw CkError.convertError(error);
  }

  render() {
    const { children } = this.props;

    return children;
  }
}
