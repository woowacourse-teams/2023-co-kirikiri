import { wavyAnimation } from '@/styles/animations';
import styled from 'styled-components';

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
