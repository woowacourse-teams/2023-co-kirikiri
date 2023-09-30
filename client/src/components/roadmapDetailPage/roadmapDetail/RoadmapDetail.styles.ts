import styled from 'styled-components';

export const RoadmapDetail = styled.div`
  padding: 2rem 0 4rem 0;
`;

export const RoadmapInfo = styled.div`
  margin-bottom: 4rem;
`;

export const Title = styled.div`
  ${({ theme }) => theme.fonts.title_large};
  margin-bottom: 2rem;
  color: ${({ theme }) => theme.colors.main_dark};
`;

export const Description = styled.div`
  display: flex;
`;
