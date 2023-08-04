import { Suspense } from 'react';
import Categories from '../categories/Categories';

import * as S from './RoadmapListView.styles';
import { useSelectCategory } from '@/hooks/roadmap/useSelectCategory';
import RoadmapList from '../roadmapList/RoadmapList';
import Fallback from '@components/_common/fallback/Fallback';

const RoadmapListView = () => {
  const [selectedCategoryId, selectCategory] = useSelectCategory();

  return (
    <S.RoadmapListView aria-label='로드맵 뷰'>
      <Categories
        selectedCategoryId={selectedCategoryId}
        selectCategory={selectCategory}
        aria-label='카테고리 선택'
      />
      <Suspense fallback={<Fallback />}>
        <RoadmapList selectedCategoryId={selectedCategoryId} aria-label='로드맵 리스트' />
      </Suspense>
    </S.RoadmapListView>
  );
};

export default RoadmapListView;
