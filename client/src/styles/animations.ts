import { keyframes } from 'styled-components';

export const shake = keyframes`
  10%, 90% {
    transform: translate3d(-0.5px, 0, 0) rotate(-0.5deg);
  }

  20%, 80% {
    transform: translate3d(1px, 0, 0) rotate(0.5deg);
  }

  30%, 50%, 70% {
    transform: translate3d(-0.5px, 0, 0) rotate(-0.5deg);
  }

  40%, 60% {
    transform: translate3d(1px, 0, 0) rotate(0.5deg);
  }
`;

export const spin = keyframes`
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
`;
