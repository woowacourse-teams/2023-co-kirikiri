import { DialogContextType } from '@/myTypes/_common/dialog';
import { createContext } from 'react';

export const DialogContext = createContext<DialogContextType>({
  isOpen: false,
  openDialog: () => {},
  closeDialog: () => {},
});
