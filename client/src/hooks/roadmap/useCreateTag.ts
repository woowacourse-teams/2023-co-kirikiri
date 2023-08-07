import { TAG_LIMIT } from '@/constants/roadmap/tag';
import React, { useRef, useState } from 'react';

export const useCreateTag = () => {
  const [tags, setTags] = useState<string[]>([]);
  const ref = useRef<HTMLInputElement | null>(null);

  const getAddedTagText = (e: React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault();
    if (ref.current === null) return;
    if (ref.current.value === '') return;

    setTags((prev) => {
      return [...prev, ref.current?.value as string];
    });
    ref.current.value = '';
  };

  const checkIsTagCountMax = () => {
    return tags.length < TAG_LIMIT;
  };

  return { tags, ref, getAddedTagText, checkIsTagCountMax };
};