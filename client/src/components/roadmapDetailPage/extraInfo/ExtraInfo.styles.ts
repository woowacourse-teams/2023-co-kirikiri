import styled from 'styled-components';
import media from '@styles/media';

export const ExtraInfo = styled.div`
  ${({ theme }) => theme.fonts.description5};
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: space-between;

  width: 30%;
  height: 55rem;

  ${media.mobile`
    display: none;
  `}
`;

export const RoadmapMetadata = styled.div`
  display: flex;
  flex-direction: column;

  & > div:not(:last-child) {
    margin-bottom: 3rem;
  }
`;

export const Category = styled.div`
  display: flex;
  align-items: center;
  justify-content: flex-end;
`;

export const Difficulty = styled.div`
  text-align: end;
`;

export const RecommendedRoadmapPeriod = styled.div`
  text-align: end;
`;

export const Tags = styled.div`
  color: ${({ theme }) => theme.colors.main_dark};
`;
