import ErrorBoundary from './ErrorBoundary';

export class CriticalErrorBoundary extends ErrorBoundary {
  static getDerivedStateFromError(error: Error): { didCatch: boolean; error: Error } {
    return { didCatch: true, error };
  }

  render() {
    const { didCatch, fallback: innerFallback } = this.state;
    const { children, fallback: custumFallback } = this.props;

    if (didCatch) {
      return custumFallback ?? innerFallback ?? <div>critical error</div>;
    }
    return children;
  }
}
