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

export const wavyAnimation = keyframes`
  0%, 40%, 100% { transform: scaleY(0.4); }
  20% { transform: scaleY(1); }
`;

export const float = keyframes`
  0% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-20px);
  }
  100% {
    transform: translateY(0);
  }
`;
