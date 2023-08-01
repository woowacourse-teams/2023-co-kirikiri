type FilterCondType = 'LATEST';

export type RoadmapListRequest = {
  lastValue: number | null;
  size: number;
  filterCond: FilterCondType;
};
