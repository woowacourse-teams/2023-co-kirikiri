import { Suspense } from 'react';
import Categories from '../categories/Categories';

import * as S from './RoadmapListView.styles';
import { useSelectCategory } from '@/hooks/roadmap/useSelectCategory';
import RoadmapList from '../roadmapList/RoadmapList';
import Fallback from '@components/_common/fallback/Fallback';

const RoadmapListView = () => {
  const [selectedCategoryId, selectCategory] = useSelectCategory();

  return (
    <S.RoadmapListView>
      <Categories
        selectedCategoryId={selectedCategoryId}
        selectCategory={selectCategory}
      />
      <Suspense fallback={<Fallback />}>
        <RoadmapList selectedCategoryId={selectedCategoryId} />
      </Suspense>
    </S.RoadmapListView>
  );
};

export default RoadmapListView;
