import { SelectedCategoryId } from '@/myTypes/roadmap/internal';
import { MouseEvent, useState } from 'react';

export const useSelectCategory = () => {
  const [selectedCategoryId, setSelectedCategoryId] = useState<SelectedCategoryId>();

  const selectCategory = ({ currentTarget }: MouseEvent<HTMLDivElement>) => {
    if (!currentTarget.id) return;

    const categoryId = Number(currentTarget.id);

    if (!categoryId) {
      setSelectedCategoryId(undefined);

      return;
    }

    setSelectedCategoryId(categoryId);
  };

  return [selectedCategoryId, selectCategory] as const;
};
