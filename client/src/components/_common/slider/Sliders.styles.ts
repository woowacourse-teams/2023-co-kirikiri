import media from '@styles/media';
import styled, { css } from 'styled-components';

export const Slider = styled.div`
  position: relative;

  overflow: hidden;
  display: flex;
  align-items: center;

  width: 100%;
`;

export const Button = styled.button<{
  isHovered: boolean;
  width: number;
  height: number;
}>`
  position: absolute;

  display: flex;
  align-items: center;
  justify-content: center;

  width: ${({ width }) => `${width}rem`};
  height: ${({ height }) => `${height}rem`};

  background-color: rgba(1, 1, 1, 0.2);
  border-radius: 8px;
  box-shadow: ${({ theme }) => theme.shadows.box};

  ${({ isHovered }) =>
    media.desktop(css`
      opacity: ${isHovered ? 1 : 0};
      transition: opacity 0.2s ease;
    `)}
`;

export const PrevButton = styled(Button)<{ isFirstContentIndex: boolean; left: number }>`
  left: ${({ left }) => `${left}rem` || 0};
  display: ${({ isFirstContentIndex }) => isFirstContentIndex && 'none'};
`;

export const NextButton = styled(Button)<{ isLastContentIndex: boolean; right: number }>`
  right: ${({ right }) => `${right}rem` || 0};
  display: ${({ isLastContentIndex }) => isLastContentIndex && 'none'};
`;

export const Contents = styled.article<{ curIndex: number; length: number }>`
  transform: ${({ curIndex }) => `translateX(${-curIndex * 100}%)`};
  display: flex;
  width: ${({ length }) => `${length * 100}%`};
  transition: transform 0.3s ease;
`;

export const Content = styled.div`
  flex-shrink: 0;
  width: 100%;
`;
