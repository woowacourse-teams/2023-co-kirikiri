import media from '@styles/media';
import styled from 'styled-components';

export const RoadmapDetailPage = styled.div`
  display: grid;
  grid-template-columns: 17fr 13fr;
  column-gap: 3rem;

  margin: 10rem 0;
  padding: 0 2rem;

  ${media.mobile`
    flex-direction: column;
    align-items: center;
  `}
`;

export const RoadmapInfo = styled.div`
  width: 100%;
`;

export const NodeList = styled.div`
  width: 100%;
  min-width: 26rem;
  border-radius: 3rem;
  box-shadow: ${({ theme }) => theme.shadows.boxBig};

  ${media.mobile`
    margin-top: 3rem;
  `}
`;
