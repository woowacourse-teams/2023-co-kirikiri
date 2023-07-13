import styled from 'styled-components';
import BREAK_POINTS from '@constants/_common/breakPoints';
import px2vw from '@utils/_common/px2vw';

export const Container = styled.div`
  width: 100%;
  margin-right: auto;
  margin-left: auto;
  background: red;

  @media (min-width: ${BREAK_POINTS.MOBILE}px) {
    max-width: ${px2vw(BREAK_POINTS.MOBILE)};
  }

  @media (min-width: ${BREAK_POINTS.TABLET}px) {
    max-width: ${px2vw(BREAK_POINTS.TABLET)};
  }

  @media (min-width: ${BREAK_POINTS.DESKTOP}px) {
    max-width: ${px2vw(BREAK_POINTS.DESKTOP)};
  }
`;
