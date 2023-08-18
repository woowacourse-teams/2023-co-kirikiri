import { Suspense } from 'react';
import Categories from '../categories/Categories';

import * as S from './RoadmapListView.styles';
import { useSelectCategory } from '@/hooks/roadmap/useSelectCategory';
import RoadmapList from '../roadmapList/RoadmapList';
import Spinner from '@components/_common/spinner/Spinner';
import RoadmapSearch from '../roadmapSearch/RoadmapSearch';
// import { Select } from '@/components/roadmapCreatePage/selector/SelectBox';
import { Outlet } from 'react-router-dom';
import useValidParams from '@/hooks/_common/useValidParams';

const RoadmapListView = () => {
  const [selectedCategoryId, selectCategory] = useSelectCategory();
  const { search } = useValidParams();

  return (
    <S.RoadmapListView aria-label='로드맵 뷰'>
      {/* <S.SelectWrapper>
        <Select defaultOpen>
          <Select.OptionGroup asChild>
            <S.SearchCategoryOptionGroup>
              <Select.Option id={1} asChild>
                <S.SearchCategoryOption>태그명</S.SearchCategoryOption>
              </Select.Option>
              <Select.Option id={2} asChild>
                <S.SearchCategoryOption>로드맵 제목</S.SearchCategoryOption>
              </Select.Option>
              <Select.Option id={3} asChild>
                <S.SearchCategoryOption>크리에이터</S.SearchCategoryOption>
              </Select.Option>
            </S.SearchCategoryOptionGroup>
          </Select.OptionGroup>
        </Select>
        <p>(으)로 검색하기</p>
      </S.SelectWrapper> */}
      <RoadmapSearch />
      <Categories
        selectedCategoryId={selectedCategoryId}
        selectCategory={selectCategory}
        aria-label='카테고리 선택'
      />

      <Suspense fallback={<Spinner />}>
        <Outlet />
        {!search && (
          <RoadmapList
            selectedCategoryId={selectedCategoryId}
            aria-label='로드맵 리스트'
          />
        )}
      </Suspense>
    </S.RoadmapListView>
  );
};

export default RoadmapListView;
