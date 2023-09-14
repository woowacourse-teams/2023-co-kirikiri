import { SERVER_ERROR_CODE } from '@/constants/_common/regex';
import { ErrorInfo } from 'react';
import { ServerError } from '../error/ErrorComponents';
import ErrorBoundary from './ErrorBoundary';

class ServerErrorBoundary extends ErrorBoundary {
  componentDidCatch(error: any, _errorInfo: ErrorInfo): void {
    if (!SERVER_ERROR_CODE.test(error.response.status)) throw error;
  }

  render() {
    const { didCatch } = this.state;
    const { children } = this.props;
    if (didCatch) {
      return <ServerError />;
    }
    return children;
  }
}

export default ServerErrorBoundary;
