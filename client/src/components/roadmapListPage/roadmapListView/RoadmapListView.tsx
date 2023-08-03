import Categories from '../categories/Categories';

import * as S from './RoadmapListView.styles';
import { useSelectCategory } from '@/hooks/roadmap/useSelectCategory';
import RoadmapList from '../roadmapList/RoadmapList';

const RoadmapListView = () => {
  const [selectedCategoryId, selectCategory] = useSelectCategory();

  return (
    <S.RoadmapListView>
      <Categories
        selectedCategoryId={selectedCategoryId}
        selectCategory={selectCategory}
      />
      <RoadmapList selectedCategoryId={selectedCategoryId} />
    </S.RoadmapListView>
  );
};

export default RoadmapListView;
