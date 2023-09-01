import type { RoadmapItemType, RoadmapDetailType, RoadmapValueType } from './internal';
import { SelectedCategoryId } from './internal';

type RoadmapFilterCondition =
  | 'GOAL_ROOM_COUNT'
  | 'LATEST'
  | 'PARTICIPANT_COUNT'
  | 'REVIEW_RATE';

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
