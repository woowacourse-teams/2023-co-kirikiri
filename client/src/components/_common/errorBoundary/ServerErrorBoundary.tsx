import { ErrorInfo } from 'react';
import ErrorBoundary from './ErrorBoundary';

class ServerErrorBoundary extends ErrorBoundary {
  componentDidCatch(error: any, _errorInfo: ErrorInfo): void {
    const status = /5\d{2}/;
    if (!status.test(error.response.status)) throw error;
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

export default ServerErrorBoundary;
