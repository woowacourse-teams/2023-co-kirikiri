import { Suspense } from 'react';
import Categories from '../categories/Categories';

import * as S from './RoadmapListView.styles';
import { useSelectCategory } from '@/hooks/roadmap/useSelectCategory';
import RoadmapList from '../roadmapList/RoadmapList';
import Spinner from '@components/_common/spinner/Spinner';

const RoadmapListView = () => {
  const [selectedCategoryId, selectCategory] = useSelectCategory();

  return (
    <S.RoadmapListView aria-label='로드맵 뷰'>
      <Categories
        selectedCategoryId={selectedCategoryId}
        selectCategory={selectCategory}
        aria-label='카테고리 선택'
      />
      <S.ServiceDescWrapper>
        <S.ServiceDescContent>
          <S.ServiceDesc>
            코끼리끼리에서 나만의 로드맵을 자유롭게 제공해보세요! <br /> 전문적인 지식
            공유도 좋고, 가벼운 취미 공유도 좋습니다. <br /> 매력적인 로드맵은
            여러사람들을 움직이게 할 거 에요! <br /> 로드맵을 달성하길 원하는 사람들은
            '목표를 달성하는 방'이라는 뜻의 골룸을 생성해서 모일 수 있어요. <br />{' '}
            코끼리끼리를 통해 함께 목표를 달성해봐요!
          </S.ServiceDesc>
        </S.ServiceDescContent>
      </S.ServiceDescWrapper>
      <Suspense fallback={<Spinner />}>
        <RoadmapList selectedCategoryId={selectedCategoryId} aria-label='로드맵 리스트' />
      </Suspense>
    </S.RoadmapListView>
  );
};

export default RoadmapListView;
