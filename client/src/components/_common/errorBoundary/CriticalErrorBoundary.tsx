// import { ErrorInfo } from 'react';
import { ErrorInfo } from 'react';
import ErrorBoundary from './ErrorBoundary';

class CriticalErrorBoundary extends ErrorBoundary {
  componentDidCatch(_error: any, _errorInfo: ErrorInfo): void {}

  render() {
    const { didCatch } = this.state;
    const { children, fallbackRender } = this.props;
    if (didCatch) {
      return fallbackRender();
    }
    return children;
  }
}

export default CriticalErrorBoundary;
