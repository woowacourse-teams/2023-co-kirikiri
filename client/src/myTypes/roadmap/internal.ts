import { DIFFICULTY_ICON_NAME } from '@constants/roadmap/difficulty';
import { CategoriesInfo } from '@constants/roadmap/category';
import * as Icons from '@components/icons/svgIcons';

export type CategoryType = {
  id: keyof typeof CategoriesInfo;
  name: string;
  iconName: keyof typeof Icons;
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

export type PatternType = {
  rule: RegExp;
  message: string;
};
