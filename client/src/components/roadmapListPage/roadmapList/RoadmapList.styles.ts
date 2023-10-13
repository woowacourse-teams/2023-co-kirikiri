import media from '@/styles/media';
import styled from 'styled-components';

export const RoadmapList = styled.div`
  position: relative;

  display: grid;
  grid-gap: 1rem;
  grid-template-columns: repeat(auto-fit, minmax(30rem, auto));
  justify-items: center;

  margin-bottom: 8rem;
  min-height: 100vh;
  padding: 0 2rem;

  ${media.tablet`
  grid-template-columns: repeat(auto-fill, minmax(30rem, auto));


  `}

  ${media.mobile`
  grid-template-columns: repeat(auto-fill, minmax(30rem, auto));

  
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
