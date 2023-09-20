import { ErrorInfo } from 'react';
import { NotFound } from '../error/ErrorComponents';
import ErrorBoundary from './ErrorBoundary';

class NotFoundErrorBoundary extends ErrorBoundary {
  componentDidCatch(error: any, _errorInfo: ErrorInfo): void {
    if (error.response.status !== 404) throw error;
  }

  render() {
    const { didCatch } = this.state;
    const { children } = this.props;
    if (didCatch) {
      return <NotFound />;
    }
    return children;
  }
}

export default NotFoundErrorBoundary;
