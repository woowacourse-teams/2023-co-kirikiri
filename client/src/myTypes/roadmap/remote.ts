import type {
  CreatorType,
  CategoryType,
  ContentType,
  TagType,
  RoadmapItemType,
} from './internal';

export type RoadmapValueType = {
  categoryId: null | number;
  title: null | string;
  introduction: null | string;
  content: null | string;
  difficulty: null | number;
  requiredPeriod: null | string;
  roadmapNodes: [];
};

export type RoadmapListResponse = RoadmapItemType[];

export type RoadmapDetailResponse = {
  roadmapId: number;
  category: CategoryType;
  roadmapTitle: string;
  introduction: string;
  creator: CreatorType;
  content: ContentType;
  difficulty: string;
  recommendedRoadmapPeriod: number;
  tags: TagType[];
};
