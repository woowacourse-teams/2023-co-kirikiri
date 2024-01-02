import { SearchIcon } from '@/components/icons/svgIcons';
import { getInvariantObjectKeys, invariantOf } from '@/utils/_common/invariantType';
import { Select } from 'ck-util-components';
import { FormEvent, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import * as S from './roadmapSearch.styles';

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
  const [categoryOpen, setCategoryOpen] = useState(false);

  const selectSearchCategory = (option: keyof typeof searchCategorySelection) => {
    setSearchCategory(option);
  };

  const toggleSearchCategory = () => {
    // eslint-disable-next-line no-unused-expressions
    categoryOpen ? setCategoryOpen(false) : setCategoryOpen(true);
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
            <Select
              externalSelectedOption={searchCategory}
              onSelectChange={selectSearchCategory}
              externalOpen={categoryOpen}
              onOpenChange={toggleSearchCategory}
            >
              <S.TriggerAndOptionWrapper>
                <Select.Trigger asChild>
                  <S.SelectTrigger>
                    ⌵ {searchCategorySelection[searchCategory]}
                    <div>|</div>
                  </S.SelectTrigger>
                </Select.Trigger>
                <Select.OptionGroup asChild>
                  <S.SearchCategoryOptionGroup>
                    {getInvariantObjectKeys(invariantOf(searchCategorySelection)).map(
                      (categ) => {
                        return (
                          <Select.Option
                            id={categ}
                            onOptionClick={() => toggleSearchCategory()}
                            asChild
                          >
                            <S.SearchCategoryOption>
                              {searchCategorySelection[categ]}
                            </S.SearchCategoryOption>
                          </Select.Option>
                        );
                      }
                    )}
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
