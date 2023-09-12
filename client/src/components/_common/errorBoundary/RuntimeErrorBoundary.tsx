import { ErrorInfo } from 'react';
import ErrorBoundary from './ErrorBoundary';

class RuntimeErrorBoundary extends ErrorBoundary {
  componentDidCatch(error: any, _errorInfo: ErrorInfo): void {
    if (error.response.status !== 404) throw error;
  }

  render() {
    const { didCatch } = this.state;
    const { children, fallbackRender } = this.props;
    if (didCatch) {
      return fallbackRender();
    }
    return children;
  }
}

export default RuntimeErrorBoundary;
