import { ErrorBoundaryContextType } from '@/myTypes/_common/errorBoundary';
import { createContext } from 'react';

export const ErrorBoundaryContext = createContext<ErrorBoundaryContextType | null>(null);
