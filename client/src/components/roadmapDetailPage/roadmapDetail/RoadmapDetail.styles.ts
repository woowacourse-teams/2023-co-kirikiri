import media from '@styles/media';
import styled from 'styled-components';

export const RoadmapDetail = styled.div`
  position: relative;
  margin: 4rem 0;
  padding: 0 2rem;

  ${media.mobile`
    flex-direction: column;
    align-items: center;
  `}
`;

export const RoadmapBody = styled.p`
  ${({ theme }) => theme.fonts.description5}
  width: 100%;
  margin: 4rem 0;
  padding: 4rem 8rem;

  border-radius: 3rem;
  box-shadow: ${({ theme }) => theme.shadows.box};

  ${media.mobile`
    padding: 4rem 4rem;
  `}
`;

export const PageOnTop = styled.div`
  display: flex;
  justify-content: space-between;
`;
