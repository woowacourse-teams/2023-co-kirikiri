/* eslint-disable @typescript-eslint/no-non-null-assertion */
import React, { PropsWithChildren, createContext, useContext, useState } from 'react';
import * as S from './Sliders.styles';
import type {
  NextButtonProps,
  PrevButtonProps,
  SliderContextType,
} from '@myTypes/_common/slider';
import useHover from '@hooks/_common/useHover';

const SliderContext = createContext<SliderContextType | null>(null);

const Slider = ({ children }: PropsWithChildren) => {
  const [curIndex, setCurIndex] = useState(0);
  const [contentLength, setContentLength] = useState(1);
  const { isHovered, handleMouseEnter, handleMouseLeave } = useHover();

  const isFirstContentIndex = curIndex === 0;
  const isLastContentIndex = curIndex === contentLength - 1;

  const slideToPrevContent = () => {
    setCurIndex((prev) => {
      return isFirstContentIndex ? prev : prev - 1;
    });
  };

  const slideToNextContent = () => {
    setCurIndex((prev) => {
      return isLastContentIndex ? prev : prev + 1;
    });
  };

  return (
    <SliderContext.Provider
      value={{
        curIndex,
        setCurIndex,
        slideToPrevContent,
        slideToNextContent,
        setContentLength,
        isHovered,
        isFirstContentIndex,
        isLastContentIndex,
      }}
    >
      <S.Slider onMouseEnter={handleMouseEnter} onMouseLeave={handleMouseLeave}>
        {children}
      </S.Slider>
    </SliderContext.Provider>
  );
};

const PrevButton = ({ children, left, width, height }: PrevButtonProps) => {
  const { slideToPrevContent, isHovered, isFirstContentIndex } =
    useContext(SliderContext)!;

  return (
    <S.PrevButton
      onClick={slideToPrevContent}
      isHovered={isHovered}
      isFirstContentIndex={isFirstContentIndex}
      left={left}
      width={width}
      height={height}
    >
      {children}
    </S.PrevButton>
  );
};

const NextButton = ({ children, right, width, height }: NextButtonProps) => {
  const { slideToNextContent, isHovered, isLastContentIndex } =
    useContext(SliderContext)!;

  return (
    <S.NextButton
      onClick={slideToNextContent}
      isHovered={isHovered}
      isLastContentIndex={isLastContentIndex}
      right={right}
      width={width}
      height={height}
    >
      {children}
    </S.NextButton>
  );
};

const Contents = ({ children }: PropsWithChildren) => {
  const { curIndex, setContentLength } = useContext(SliderContext)!;
  // ReactElement이길 기대하지만 다른 타입이 오더라도(string 등) 정상적으로 동작합니다
  const childrenArray = React.Children.toArray(children) as React.ReactElement[];
  setContentLength(childrenArray.length);

  return (
    <S.Contents curIndex={curIndex} length={childrenArray.length}>
      {childrenArray.map((child, index) => (
        <S.Content key={child?.props?.id || index}>{child}</S.Content>
      ))}
    </S.Contents>
  );
};

Slider.PrevButton = PrevButton;
Slider.NextButton = NextButton;
Slider.Contents = Contents;

export default Slider;
