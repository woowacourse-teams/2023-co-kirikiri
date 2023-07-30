import { useRef, useState } from 'react';

export const useCreateTag = () => {
  const [tags, setTags] = useState<string[]>([]);
  const ref = useRef<HTMLInputElement | null>(null);

  const getTagText = () => {
    if (ref.current === null) return;
    if (ref.current.value === '') return;

    setTags((prev) => [...prev, ref.current?.value as string]);
    ref.current.value = '';
  };

  const checkTagCountUnderFive = () => {
    return tags.length < 4;
  };

  return { tags, ref, getTagText, checkTagCountUnderFive };
};
