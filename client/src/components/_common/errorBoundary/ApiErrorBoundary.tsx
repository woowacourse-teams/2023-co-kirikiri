import { AxiosError } from 'axios';
import { ErrorInfo } from 'react';
import { Navigate } from 'react-router-dom';
import { Forbidden, NotFound } from '../fallback/Fallback';
import ErrorBoundary from './ErrorBoundary';
import { APIError } from './errors';

export class ApiErrorBoundary<T extends APIError> extends ErrorBoundary {
  static getDerivedStateFromError(error: Error): { didCatch: boolean; error: Error } {
    if (error instanceof APIError) {
      return { didCatch: true, error };
    }
    throw error;
  }

  componentDidCatch(
    error: T extends APIError ? AxiosError : Error,
    errorInfo: ErrorInfo
  ): void {
    if (this.props.isCritical) throw error;

    this.props.onError?.(error, errorInfo);

    switch (error.response?.status) {
      case 401:
        this.setState({ fallback: <Navigate to='/login' /> });
        break;
      case 403:
        this.setState({ fallback: <Forbidden /> });
        break;
      case 404:
        this.setState({ fallback: <NotFound /> });
        break;
      default:
        break;
    }
  }

  render() {
    const { didCatch, fallback: innerFallback } = this.state;
    const { children, fallback: custumFallback } = this.props;
    if (didCatch) {
      return (
        custumFallback ??
        innerFallback ?? <div>데이터를 요청할 수 없습니다. 잠시후 다시 시도해주세요</div>
      );
    }
    return children;
  }
}
