import { ErrorBoundaryProps, ErrorBoundaryState } from '@/myTypes/_common/errorBoundary';
import { Component } from 'react';

const initialState: ErrorBoundaryState = {
  didCatch: false,
  error: null,
};

class ErrorBoundary extends Component<any, any> {
  constructor(props: ErrorBoundaryProps) {
    super(props);

    this.state = initialState;
  }

  static getDerivedStateFromError(error: Error) {
    return { didCatch: true, error };
  }
}

export default ErrorBoundary;
