import Categories from '../categories/Categories';
import * as S from './RoadmapListView.styles';
import RoadmapList from '../roadmapList/RoadmapList';
import RoadmapSearch from '../roadmapSearch/RoadmapSearch';
import { Link, Outlet, useLocation } from 'react-router-dom';
import SVGIcon from '@/components/icons/SVGIcon';
import AsyncBoundary from '@/components/_common/errorBoundary/AsyncBoundary';
import RoadmapSearchResult from '../roadmapSearch/RoadmapSearchResult';
import QueryString from 'qs';

const RoadmapListView = () => {
  const location = useLocation();
  const { searchWord } = QueryString.parse(location.search, {
    ignoreQueryPrefix: true,
  });

  return (
    <S.RoadmapListView aria-label='로드맵 뷰'>
      <Link to='/roadmap-list'>
        <S.ListTitle>
          로드맵 둘러보기 <SVGIcon name='RoadmapIcon' color='#76a982' />
        </S.ListTitle>
      </Link>
      <RoadmapSearch />
      <Categories aria-label='카테고리 선택' />

      <AsyncBoundary>
        <Outlet />
        {!searchWord ? (
          <RoadmapList aria-label='로드맵 리스트' />
        ) : (
          <RoadmapSearchResult />
        )}
      </AsyncBoundary>
    </S.RoadmapListView>
  );
};

export default RoadmapListView;
