import { SearchIcon } from '@/components/icons/svgIcons';
import { Select } from '@/components/roadmapCreatePage/selector/SelectBox';
import { KeyboardEvent, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import * as S from './roadmapSearch.styles';

const searchCategoryKeyword = {
  1: 'tagName',
  2: 'roadmapTitle',
  3: 'creatorName',
} as const;

const RoadmapSearch = () => {
  const searchWordRef = useRef<HTMLInputElement | null>(null);
  const navigate = useNavigate();
  const [searchCategory, setSearchCategory] = useState<
    'tagName' | 'roadmapTitle' | 'creatorName'
  >('roadmapTitle');

  const selectSearchCategory = (id: number) => {
    // eslint-disable-next-line no-prototype-builtins
    if (searchCategory.hasOwnProperty(id)) {
      setSearchCategory(searchCategoryKeyword[id as keyof typeof searchCategoryKeyword]);
    }
  };

  const checkIsEnterKey = (e: KeyboardEvent<HTMLInputElement>) => {
    return e.code === 'Enter';
  };

  const searchRoadmap = () => {
    if (searchWordRef.current?.value === '') return;

    navigate(`/roadmap-list/${searchCategory}/${searchWordRef.current?.value}`);
  };

  const resetSearchResult = () => {
    navigate('/roadmap-list');
  };

  return (
    <>
      <S.SelectWrapper>
        <Select defaultOpen externalSelectState={selectSearchCategory}>
          <Select.OptionGroup asChild>
            <S.SearchCategoryOptionGroup>
              <Select.Option id={1} asChild defaultOpen>
                <S.SearchCategoryOption>태그명</S.SearchCategoryOption>
              </Select.Option>
              <Select.Option id={2} asChild defaultSelected defaultOpen>
                <S.SearchCategoryOption>로드맵 제목</S.SearchCategoryOption>
              </Select.Option>
              <Select.Option id={3} asChild defaultOpen>
                <S.SearchCategoryOption>크리에이터</S.SearchCategoryOption>
              </Select.Option>
            </S.SearchCategoryOptionGroup>
          </Select.OptionGroup>
        </Select>
        <p>(으)로 검색하기</p>
      </S.SelectWrapper>
      <S.InputFlex>
        <S.Wrapper>
          <S.InputWrapper>
            <S.SearchInput
              placeholder='로드맵을 검색해주세요'
              maxLength={20}
              ref={searchWordRef}
              onKeyPress={(e: KeyboardEvent<HTMLInputElement>) =>
                checkIsEnterKey(e) && searchRoadmap()
              }
            />
          </S.InputWrapper>
          <S.SearchButton onClick={searchRoadmap} aria-label='검색버튼'>
            <SearchIcon width='30px' height='30px' />
          </S.SearchButton>
        </S.Wrapper>
        <S.ResetSearchButton onClick={resetSearchResult}>
          전체결과로 돌아가기
        </S.ResetSearchButton>
      </S.InputFlex>
    </>
  );
};

export default RoadmapSearch;
