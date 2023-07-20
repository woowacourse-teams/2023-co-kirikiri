import { DIFFICULTY_ICON_NAME } from '@constants/roadmap/Difficulty';
import { CategoriesInfo } from '@constants/roadmap/Category';

export type CategoryType = {
  id: keyof typeof CategoriesInfo;
  name: string;
};

export type CreatorType = {
  id: number;
  name: string;
};

export type RoadmapItemType = {
  roadmapId: number;
  roadmapTitle: string;
  introduction: string;
  difficulty: keyof typeof DIFFICULTY_ICON_NAME;
  recommendedRoadmapPeriod: number;
  creator: CreatorType;
  category: CategoryType;
};

export type RoadmapListResponseType = {
  currentPage: number;
  totalPage: number;
  data: RoadmapItemType[];
};
