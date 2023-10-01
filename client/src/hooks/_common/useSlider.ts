import React, { useState, ReactNode } from 'react';

const useSlider = (children: ReactNode) => {
  const [curIndex, setCurIndex] = useState(0);
  const childrenArray = React.Children.toArray(children) as React.ReactElement[];
  const contentLength = childrenArray.length;

  const isFirstContentIndex = curIndex === 0;
  const isLastContentIndex = curIndex === contentLength - 1;

  const slideToPrevContent = () => {
    setCurIndex((prev) => (isFirstContentIndex ? prev : prev - 1));
  };

  const slideToNextContent = () => {
    setCurIndex((prev) => (isLastContentIndex ? prev : prev + 1));
  };

  return {
    curIndex,
    slideToPrevContent,
    slideToNextContent,
    isFirstContentIndex,
    isLastContentIndex,
    childrenArray,
  };
};

export default useSlider;
