import { useState } from 'react';

export const useSelect = <T>(defaultOpen?: boolean) => {
  const [selectedOption, setSelectedOption] = useState<T | null>(null);
  const [isSelecBoxOpen, setIsSelecBoxOpen] = useState(defaultOpen ?? false);

  const toggleBoxOpen = () => {
    setIsSelecBoxOpen((prevIsOpen) => !prevIsOpen);
  };

  const selectOption = (option: T) => {
    setSelectedOption(option);
  };

  return { selectedOption, selectOption, isSelecBoxOpen, toggleBoxOpen };
};
