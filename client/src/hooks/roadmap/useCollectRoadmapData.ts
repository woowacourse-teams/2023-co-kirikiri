import { DummyCategoryType } from '@/components/roadmapCreatePage/category/Category';
import { DummyDifficultyType } from '@/components/roadmapCreatePage/difficulty/Difficulty';
import { RoadmapValueType } from '@/myTypes/roadmap/remote';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCreateRoadmap } from '../queries/roadmap';

export const useCollectRoadmapData = () => {
  const [roadmapValue, setRoadmapValue] = useState<RoadmapValueType>({
    categoryId: null,
    title: null,
    introduction: null,
    content: null,
    difficulty: null,
    requiredPeriod: null,
    roadmapTags: [],
    roadmapNodes: [],
  });
  const [isSumbited, setIsSubmited] = useState(false);
  const navigate = useNavigate();
  const { createRoadmap } = useCreateRoadmap();

  const getSelectedCategoryId = (category: keyof DummyCategoryType | null) => {
    setRoadmapValue((prev) => ({
      ...prev,
      categoryId: category,
    }));
  };

  const getSelectedDifficulty = (difficulty: keyof DummyDifficultyType | null) => {
    setRoadmapValue((prev) => ({
      ...prev,
      difficulty,
    }));
  };

  const addNode = (e: React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault();

    setRoadmapValue((prev) => {
      const tempRoadmapNodes = [...prev.roadmapNodes, {}];
      return { ...prev, roadmapNodes: tempRoadmapNodes };
    });
  };

  const getRoadmapItemTitle = (
    e: React.ChangeEvent<HTMLInputElement>,
    itemId: number
  ) => {
    setRoadmapValue((prev) => {
      const inputName = e.target.name;
      const tempRoadmapNodes = [...prev.roadmapNodes];

      tempRoadmapNodes[itemId] = { ...tempRoadmapNodes[itemId] };
      tempRoadmapNodes[itemId][inputName] = e.target.value;

      return { ...prev, roadmapNodes: tempRoadmapNodes };
    });
  };

  const getTags = (tags: string[]) => {
    setRoadmapValue((prev) => {
      const newTags = tags.map((tag) => {
        return { name: tag };
      });

      return { ...prev, roadmapTags: [...newTags] };
    });
  };

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    const target = e.target as HTMLFormElement;

    setRoadmapValue((prev) => ({
      ...prev,
      title: target.roadmapTitle.value,
      introduction: target.introduction.value,
      content: target.content.value,
      requiredPeriod: target.requiredPeriod.value,
    }));
    setIsSubmited(true);
  };

  useEffect(() => {
    if (isSumbited) {
      createRoadmap(roadmapValue);
      navigate('/');
    }
  }, [isSumbited]);

  return {
    roadmapValue,
    getSelectedCategoryId,
    getSelectedDifficulty,
    getRoadmapItemTitle,
    getTags,
    handleSubmit,
    addNode,
  };
};
