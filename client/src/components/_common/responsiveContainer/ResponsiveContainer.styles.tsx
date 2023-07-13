import styled from 'styled-components';
import BREAK_POINTS from '@constants/_common/breakPoints';
import pxToVw from '@utils/_common/pxToVw';

export const Container = styled.div`
  width: 100%;
  margin-right: auto;
  margin-left: auto;
  background: red;

  @media (min-width: ${BREAK_POINTS.MOBILE}px) {
    max-width: ${pxToVw(BREAK_POINTS.MOBILE)};
  }

  @media (min-width: ${BREAK_POINTS.TABLET}px) {
    max-width: ${pxToVw(BREAK_POINTS.TABLET)};
  }

  @media (min-width: ${BREAK_POINTS.DESKTOP}px) {
    max-width: ${pxToVw(BREAK_POINTS.DESKTOP)};
  }
`;
