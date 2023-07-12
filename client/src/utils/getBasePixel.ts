import BREAK_POINTS from '@constants/breakPoints';

const getBasePixel = () => {
  const viewportWidth = window.innerWidth;

  if (viewportWidth >= BREAK_POINTS.DESKTOP) {
    return 16;
  }

  if (viewportWidth >= BREAK_POINTS.TABLET) {
    return 18;
  }

  return 24;
};

export default getBasePixel;
