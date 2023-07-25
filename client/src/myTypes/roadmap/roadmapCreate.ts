export type PatternType = {
  rule: RegExp;
  message: string;
};

export type RaodmapValueType = {
  categoryId: null | number;
  title: null | string;
  introduction: null | string;
  content: null | string;
  difficulty: null | number;
  requiredPeriod: null | string;
  roadmapNodes: [];
};
