import * as S from './roadmapSearch.styles';

const RoadmapSearch = () => {
  return (
    <S.Wrapper>
      <S.InputWrapper>
        <S.SearchInput placeholder='로드맵을 검색해주세요' maxLength={20} />
      </S.InputWrapper>
      <S.SearchButton>search</S.SearchButton>
    </S.Wrapper>
  );
};

export default RoadmapSearch;
