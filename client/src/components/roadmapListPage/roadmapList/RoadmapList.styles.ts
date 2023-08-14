import styled from 'styled-components';
import { wavyAnimation } from '@styles/animations';

export const RoadmapList = styled.div`
  display: flex;
  flex-wrap: wrap;
  row-gap: 3rem;
  justify-content: space-around;

  margin-bottom: 8rem;
`;

export const CreateRoadmapButton = styled.button`
  ${({ theme }) => theme.fonts.h2}
  position: fixed;
  top: 1rem;

  width: 30%;
  height: 5rem;

  color: ${({ theme }) => theme.colors.white};

  background-color: ${({ theme }) => theme.colors.main_middle};
  border-radius: 20px;
`;

export const WavyLoading = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;

  div {
    transform-origin: 50% 100%;

    display: inline-block;

    width: 1rem;
    height: 2rem;
    margin: 0.3rem;

    background-color: ${({ theme }) => theme.colors.main_middle};
    border-radius: 50%;

    animation: ${wavyAnimation} 1s infinite ease-in-out;
  }

  div:nth-child(1) {
    animation-delay: -0.3s;
  }
  div:nth-child(2) {
    animation-delay: -0.2s;
  }
  div:nth-child(3) {
    animation-delay: -0.1s;
  }
`;
