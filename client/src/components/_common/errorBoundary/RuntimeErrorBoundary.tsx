import { ErrorInfo } from 'react';
import { Runtime } from '../error/ErrorComponents';
import ErrorBoundary from './ErrorBoundary';

class RuntimeErrorBoundary extends ErrorBoundary {
  componentDidCatch(_error: any, _errorInfo: ErrorInfo): void {}

  render() {
    const { didCatch } = this.state;
    const { children } = this.props;
    if (didCatch) {
      return <Runtime />;
    }
    return children;
  }
}

export default RuntimeErrorBoundary;
