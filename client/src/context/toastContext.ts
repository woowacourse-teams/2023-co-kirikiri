import { ToastContextType } from '@/myTypes/_common/toast';
import { createContext } from 'react';

export const ToastContext = createContext<ToastContextType>({
  triggerToast: () => {},
});
