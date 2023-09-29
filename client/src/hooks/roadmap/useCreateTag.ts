import { TAG_LIMIT } from '@/constants/roadmap/tag';
import React, { useRef, useState } from 'react';

export const useCreateTag = () => {
  const [tags, setTags] = useState<string[]>([]);
  const ref = useRef<HTMLInputElement | null>(null);

  const getAddedTagText = () => {
    if (ref.current === null) return;
    if (ref.current.value === '') return;

    setTags((prev) => {
      return [...prev, ref.current?.value as string];
    });
    ref.current.value = '';
  };

  const addTagByButton = (e: React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault();
    getAddedTagText();
  };

  const addTagByEnter = (e: React.KeyboardEvent) => {
    if (e.code === 'Enter') {
      e.preventDefault();
      getAddedTagText();
    }
  };

  const checkIsTagCountMax = () => {
    return tags.length < TAG_LIMIT;
  };

  const checkIsAddCountMax = () => {
    return tags.length < TAG_LIMIT - 1;
  };

  const deleteTag = (e: React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault();

    const target = e.target as HTMLButtonElement;

    setTags((prev) => {
      return prev.filter((tag) => tag !== target.value);
    });
  };
  return {
    tags,
    ref,
    addTagByButton,
    addTagByEnter,
    checkIsTagCountMax,
    checkIsAddCountMax,
    deleteTag,
  };
};
