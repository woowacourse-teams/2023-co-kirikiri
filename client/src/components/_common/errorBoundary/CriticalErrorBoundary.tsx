import { ErrorInfo } from 'react';
import { Critical } from '../error/ErrorComponents';
import ErrorBoundary from './ErrorBoundary';

class CriticalErrorBoundary extends ErrorBoundary {
  componentDidCatch(_error: any, _errorInfo: ErrorInfo): void {}

  render() {
    const { didCatch } = this.state;
    const { children } = this.props;
    if (didCatch) {
      return <Critical />;
    }
    return children;
  }
}

export default CriticalErrorBoundary;
