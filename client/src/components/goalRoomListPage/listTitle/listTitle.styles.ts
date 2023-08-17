import styled from 'styled-components';

export const Container = styled.section``;

export const RaodmapTitle = styled.h1`
  ${({ theme }) => theme.fonts.title_large}
`;

export const RoadmapCreator = styled.h2`
  ${({ theme }) => theme.fonts.button2}
  color: ${({ theme }) => theme.colors.gray200};
`;

export const RoadmapIntroduce = styled.div`
  ${({ theme }) => theme.fonts.description5};
  margin-top: 3.5rem;
`;
