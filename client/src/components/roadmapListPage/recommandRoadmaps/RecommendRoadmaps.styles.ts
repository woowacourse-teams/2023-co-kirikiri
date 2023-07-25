import { styled } from 'styled-components';

export const RecommendRoadmaps = styled.div``;

export const Title = styled.div`
  ${({ theme }) => theme.fonts.title_large}
  & > span {
    color: ${({ theme }) => theme.colors.main_dark};
  }
`;

export const Slider = styled.div`
  overflow: auto;
  display: flex;
  padding: 3rem;

  & > div:not(:last-child) {
    margin-right: 4rem;
  }
`;
