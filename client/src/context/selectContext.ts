import { createContext } from 'react';
import { SelectContextType } from '@/types/_common/select';

export const SelectContext = createContext<SelectContextType>({
  isSelecBoxOpen: false,
  toggleBoxOpen: () => {},
  selectedId: null,
  selectOption: (_id: number) => {},
});
