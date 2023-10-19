import { DIFFICULTY_ICON_NAME } from '@constants/roadmap/difficulty';
import { CategoriesInfo } from '@constants/roadmap/category';

export type DifficultyKeyType =
  | 'VERY_EASY'
  | 'EASY'
  | 'NORMAL'
  | 'DIFFICULT'
  | 'VERY_DIFFICULT';

export type DifficultyValueType = '매우쉬움' | '쉬움' | '보통' | '어려움' | '매우어려움';

export type DifficultiesType = { [key in DifficultyKeyType]: DifficultyValueType };

export type CategoryType = {
  id: keyof typeof CategoriesInfo;
  name: string;
};

export type CreatorType = {
  id: number;
  name: string;
};

export type TagType = {
  id: number;
  name: string;
};

export type NodeType = {
  id: number;
  title: string;
  description: string;
  imageUrls: string[];
};

export type ContentType = {
  id: number;
  content: string;
  nodes: NodeType[];
};

export type SelectedCategoryId = number | undefined;

export type RoadmapItemType = {
  roadmapId: number;
  roadmapTitle: string;
  introduction: string;
  difficulty: keyof typeof DIFFICULTY_ICON_NAME;
  recommendedRoadmapPeriod: number;
  creator: CreatorType;
  category: CategoryType;
  createdAt: string;
  tags: TagType[];
};

export type RoadmapDetailType = {
  roadmapId: number;
  category: CategoryType;
  roadmapTitle: string;
  introduction: string;
  creator: CreatorType;
  content: ContentType;
  difficulty: keyof typeof DIFFICULTY_ICON_NAME;
  recommendedRoadmapPeriod: number;
  tags: TagType[];
};

export type RoadmapNodes = {
  [key: string]: string;
};

export type RoadmapValueType = {
  categoryId: null | number;
  title: null | string;
  introduction: null | string;
  content: null | string;
  difficulty: null | DifficultyKeyType;
  requiredPeriod: null | string;
  roadmapTags: { name: string }[];
  roadmapNodes: RoadmapNodes[];
};

export type NodeImagesType = {
  [key: number]: (string | Blob)[];
};

export type PatternType = {
  rule: RegExp;
  message: string;
};
