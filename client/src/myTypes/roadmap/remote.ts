import type { CreatorType, CategoryType, ContentType, TagsType } from './internal';

export type RoadmapValueType = {
  categoryId: null | number;
  title: null | string;
  introduction: null | string;
  content: null | string;
  difficulty: null | number;
  requiredPeriod: null | string;
  roadmapNodes: [];
};

type ResponseCategoryType = Pick<CategoryType, 'id' | 'name'>;

export type RoadmapDetailResponse = {
  roadmapId: number;
  category: ResponseCategoryType;
  roadmapTitle: string;
  introduction: string;
  creator: CreatorType;
  content: ContentType;
  difficulty: string;
  recommendedRoadmapPeriod: number;
  tags: TagsType;
};
