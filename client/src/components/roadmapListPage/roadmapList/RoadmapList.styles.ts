import media from '@/styles/media';
import styled from 'styled-components';

export const RoadmapList = styled.div`
  display: grid;
  grid-gap: 3rem;
  grid-template-columns: 1fr 1fr;
  justify-items: center;

  margin-bottom: 8rem;
  min-height: 100vh;

  ${media.mobile`
  grid-template-columns: 1fr;
  
  `}
`;

export const CreateRoadmapButton = styled.button`
  ${({ theme }) => theme.fonts.nav_title}
  position: fixed;
  right: 5%;
  bottom: 1rem;

  width: 5rem;
  height: 5rem;

  color: ${({ theme }) => theme.colors.white};

  background-color: ${({ theme }) => theme.colors.main_middle};
  border-radius: 50%;
`;
