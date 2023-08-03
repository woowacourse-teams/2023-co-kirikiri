import { SelectedCategoryId } from '@/myTypes/roadmap/internal';
import { MouseEvent, useState } from 'react';

export const useSelectCategory = () => {
  const [selectedCategoryId, setSelectedCategoryId] = useState<SelectedCategoryId>();

  const selectCategory = ({ currentTarget }: MouseEvent<HTMLDivElement>) => {
    if (!currentTarget.id) return;

    setSelectedCategoryId(Number(currentTarget.id));
  };

  return [selectedCategoryId, selectCategory] as const;
};
