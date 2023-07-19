import styled from 'styled-components';
import BREAK_POINTS from '@constants/_common/breakPoints';

export const Container = styled.div`
  width: 100%;
  margin-right: auto;
  margin-left: auto;
  /* background: red; */

  @media (min-width: ${BREAK_POINTS.TABLET}px) {
    max-width: 100vw;
  }

  @media (min-width: ${BREAK_POINTS.DESKTOP}px) {
    max-width: 1137px;
  }
`;
