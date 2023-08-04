import type {
  // CreatorType,
  // CategoryType,
  // ContentType,
  // TagType,
  RoadmapItemType,
  RoadmapDetailType,
} from './internal';

type RoadmapNodes = {
  [key: string]: string;
};

export type RoadmapValueType = {
  categoryId: null | number;
  title: null | string;
  introduction: null | string;
  content: null | string;
  difficulty: null | number;
  requiredPeriod: null | string;
  roadmapTags: { name: string }[];
  roadmapNodes: RoadmapNodes[];
};

export type RoadmapListResponse = RoadmapItemType;

export type RoadmapDetailResponse = RoadmapDetailType;

export type RoadmapValueRequest = RoadmapValueType;
