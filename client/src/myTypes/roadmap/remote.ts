import type {
  // CreatorType,
  // CategoryType,
  // ContentType,
  // TagType,
  RoadmapItemType,
  RoadmapDetailType,
} from './internal';
import { SelectedCategoryId } from './internal';

type RoadmapFilterCondition =
  | 'GOAL_ROOM_COUNT'
  | 'LATEST'
  | 'PARTICIPANT_COUNT'
  | 'REVIEW_RATE';

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

export type RoadmapListRequest = {
  categoryId?: SelectedCategoryId;
  size?: number;
  filterCond?: RoadmapFilterCondition;
  lastId?: number | null;
};

export type RoadmapListResponse = {
  responses: RoadmapItemType[];
  hasNext: boolean;
};

export type RoadmapDetailResponse = RoadmapDetailType;

export type RoadmapValueRequest = RoadmapValueType;
