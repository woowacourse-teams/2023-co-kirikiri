import BREAK_POINTS from '@constants/_common/breakPoints';

const pxToVw = (size: number, width = BREAK_POINTS.DESKTOP) =>
  `${(size / width) * 100}vw`;

export default pxToVw;
