import { SearchIcon } from '@/components/icons/svgIcons';
import { Select } from '@/components/roadmapCreatePage/selector/SelectBox';
import { FormEvent, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import * as S from './roadmapSearch.styles';

const searchCategoryKeyword = {
  1: 'tagName',
  2: 'roadmapTitle',
  3: 'creatorName',
} as const;

const searchCategorySelection = {
  tagName: '태그',
  roadmapTitle: '로드맵 제목',
  creatorName: '크리에이터',
};

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

  const searchRoadmap = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (searchWordRef.current?.value === '') return;

    navigate(`/roadmap-list/${searchCategory}/${searchWordRef.current?.value}`);
  };

  return (
    <>
      <form onSubmit={(e: FormEvent<HTMLFormElement>) => searchRoadmap(e)}>
        <S.Wrapper>
          <S.SelectWrapper>
            <Select externalSelectState={selectSearchCategory}>
              <S.TriggerAndOptionWrapper>
                <Select.Trigger asChild>
                  <S.SelectTrigger>
                    ⌵ {searchCategorySelection[searchCategory]}
                    <div>|</div>
                  </S.SelectTrigger>
                </Select.Trigger>
                <Select.OptionGroup asChild>
                  <S.SearchCategoryOptionGroup>
                    <Select.Option id={1} asChild>
                      <S.SearchCategoryOption>태그</S.SearchCategoryOption>
                    </Select.Option>
                    <Select.Option id={2} asChild defaultSelected>
                      <S.SearchCategoryOption>로드맵 제목</S.SearchCategoryOption>
                    </Select.Option>
                    <Select.Option id={3} asChild>
                      <S.SearchCategoryOption>크리에이터</S.SearchCategoryOption>
                    </Select.Option>
                  </S.SearchCategoryOptionGroup>
                </Select.OptionGroup>
              </S.TriggerAndOptionWrapper>
            </Select>
            <S.InputWrapper>
              <S.SearchInput
                placeholder='로드맵을 검색해주세요'
                maxLength={20}
                ref={searchWordRef}
              />
            </S.InputWrapper>
          </S.SelectWrapper>
          <S.SearchButton aria-label='검색버튼' type='submit'>
            <SearchIcon width='30px' height='30px' />
          </S.SearchButton>
        </S.Wrapper>
      </form>
    </>
  );
};

export default RoadmapSearch;
