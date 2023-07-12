import BREAK_POINTS from '@constants/breakPoints';

const px2vw = (size: number, width = BREAK_POINTS.DESKTOP) => `${(size / width) * 100}vw`;

export default px2vw;
