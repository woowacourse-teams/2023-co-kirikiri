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

  // eslint-disable-next-line react/no-unused-class-component-methods
  resetError = () => {
    // eslint-disable-next-line react/destructuring-assignment
    if (this.state.didCatch) {
      this.setState(initialState);
    }
  };
}

export default ErrorBoundary;
