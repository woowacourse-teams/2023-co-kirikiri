import Categories from '../categories/Categories';
import * as S from './RoadmapListView.styles';
import { useSelectCategory } from '@/hooks/roadmap/useSelectCategory';
import RoadmapList from '../roadmapList/RoadmapList';
import RoadmapSearch from '../roadmapSearch/RoadmapSearch';
import { Link, Outlet } from 'react-router-dom';
import useValidParams from '@/hooks/_common/useValidParams';
import SVGIcon from '@/components/icons/SVGIcon';
import AsyncBoundary from '@/components/_common/errorBoundary/AsyncBoundary';

const RoadmapListView = () => {
  const [selectedCategoryId] = useSelectCategory();
  const { search } = useValidParams();

  return (
    <S.RoadmapListView aria-label='로드맵 뷰'>
      <Link to='/roadmap-list'>
        <S.ListTitle>
          로드맵 둘러보기 <SVGIcon name='RoadmapIcon' color='#76a982' />
        </S.ListTitle>
      </Link>
      <RoadmapSearch />
      <Categories selectedCategoryId={selectedCategoryId} aria-label='카테고리 선택' />

      <AsyncBoundary>
        <Outlet />
        {!search && <RoadmapList aria-label='로드맵 리스트' />}
      </AsyncBoundary>
    </S.RoadmapListView>
  );
};

export default RoadmapListView;
