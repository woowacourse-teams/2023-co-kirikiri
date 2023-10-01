import media from '@styles/media';
import styled from 'styled-components';

export const IntroductionWrapper = styled.div`
  width: 70%;

  ${media.mobile`
    width:100%;
  `}
`;

export const Introduction = styled.div<{ isExpanded: boolean }>`
  ${({ theme }) => theme.fonts.description5};
  overflow: hidden;
  max-height: ${({ isExpanded }) => (isExpanded ? 'auto' : '55rem')};

  & > p:not(:last-child) {
    margin-bottom: 2rem;
  }

  & div {
    ${({ theme }) => theme.fonts.h1};
    margin-bottom: 0.5rem;
    color: ${({ theme }) => theme.colors.main_dark};
  }
`;

export const LineShadow = styled.div`
  position: relative;
  width: 100%;
  height: 0.2rem;
  box-shadow: 0 -4px 6px rgba(0, 0, 0, 1);
`;

export const ReadMoreButton = styled.button`
  position: relative;
  top: calc(-2rem - 4px);
  left: 50%;
  transform: translateX(-5rem);

  width: 10rem;
  height: 4rem;

  background-color: ${({ theme }) => theme.colors.white};
  border-radius: 8px;
  box-shadow: ${({ theme }) => theme.shadows.main};
`;
