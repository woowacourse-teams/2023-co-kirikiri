import { useSearchRoadmapList } from '@/hooks/queries/roadmap';
import useValidParams from '@/hooks/_common/useValidParams';

const RoadmapSearchResult = () => {
  const { category, search } = useValidParams();
  console.log(category, search);

  const tempQuery: any = {
    roadmapTitle: '',
    creatorName: '',
    tagName: '',
    filterCond: 'LATEST',
  };

  const { searchRoadmapListResponse } = useSearchRoadmapList({
    ...tempQuery,
    ...(tempQuery[category] !== undefined && { [category]: search }),
  });
  console.log(searchRoadmapListResponse);

  return <>search result</>;
};

export default RoadmapSearchResult;
