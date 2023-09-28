import styled from 'styled-components';

export const Slider = styled.div`
  position: relative;

  overflow: hidden;
  display: flex;
  align-items: center;

  width: 100%;
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
