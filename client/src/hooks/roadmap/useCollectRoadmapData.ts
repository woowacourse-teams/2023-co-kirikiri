import { CategoriesInfo } from '@/constants/roadmap/category';
import {
  DifficultyKeyType,
  NodeImagesType,
  RoadmapValueType,
} from '@/myTypes/roadmap/internal';
import { getInvariantObjectKeys, invariantOf } from '@/utils/_common/invariantType';
import { useEffect, useState } from 'react';
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
  const [nodeImages, setNodeImages] = useState<NodeImagesType>({});
  const [isSumbited, setIsSubmited] = useState(false);
  const { createRoadmap } = useCreateRoadmap();

  const getSelectedCategoryId = (category: keyof typeof CategoriesInfo) => {
    setRoadmapValue((prev) => ({
      ...prev,
      categoryId: CategoriesInfo[category].id,
    }));
  };

  const getSelectedDifficulty = (difficulty: DifficultyKeyType | null) => {
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

  const getRoadmapItemTitle = <T extends HTMLInputElement | HTMLTextAreaElement>(
    e: React.ChangeEvent<T>,
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

  const getNodeImage = (nodeImage: File, itemId: number) => {
    const tempNodeImages = structuredClone(nodeImages);
    if (itemId in tempNodeImages) {
      tempNodeImages[itemId].push(nodeImage);
    } else {
      tempNodeImages[itemId] = [nodeImage];
    }
    setNodeImages(tempNodeImages);
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
      content: target.body.value,
      requiredPeriod: target.requiredPeriod.value,
    }));
    setIsSubmited(true);
  };

  useEffect(() => {
    const formData = new FormData();

    formData.append('jsonData', JSON.stringify(roadmapValue));
    getInvariantObjectKeys(invariantOf(nodeImages)).forEach((itemId) => {
      nodeImages[itemId].forEach((imageFile) => {
        formData.append(roadmapValue.roadmapNodes[Number(itemId)].title, imageFile);
      });
    });

    if (isSumbited) {
      createRoadmap(formData);
    }
    setIsSubmited(false);
  }, [isSumbited]);

  return {
    roadmapValue,
    getSelectedCategoryId,
    getSelectedDifficulty,
    getRoadmapItemTitle,
    getNodeImage,
    getTags,
    handleSubmit,
    isSumbited,
    addNode,
  };
};
