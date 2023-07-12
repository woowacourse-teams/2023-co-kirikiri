const pxToRem = (px: number) => {
  const basePixel = parseFloat(getComputedStyle(document.documentElement).fontSize);
  return px / basePixel;
};

export default pxToRem;
