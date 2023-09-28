/* eslint-disable react/no-array-index-key */
/* eslint-disable @typescript-eslint/no-non-null-assertion */
import React, {
  Dispatch,
  PropsWithChildren,
  SetStateAction,
  createContext,
  useContext,
  useState,
} from 'react';
import * as S from './Sliders.styles';

type SliderContextType = {
  curIndex: number;
  slideToPrevContent: () => void;
  slideToNextContent: () => void;
  setCurIndex: Dispatch<SetStateAction<number>>;
  setContentLength: Dispatch<SetStateAction<number>>;
};

const SliderContext = createContext<SliderContextType | null>(null);

const Slider = ({ children }: PropsWithChildren) => {
  const [curIndex, setCurIndex] = useState(0);
  const [contentLength, setContentLength] = useState(1);

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
      }}
    >
      <S.Slider>{children}</S.Slider>
    </SliderContext.Provider>
  );
};

const PrevButton = ({ children }: PropsWithChildren) => {
  const { slideToPrevContent } = useContext(SliderContext)!;

  if (!React.isValidElement(children)) {
    throw new Error('React Element children이 필요합니다');
  }

  // React.isValidElement는 런타임에 대상이 유요한 React Element인지 확인하기 때문에 as 키워드를 사용한 타입 단언이 필요합니다
  return React.cloneElement(children as React.ReactElement, {
    onClick: slideToPrevContent,
    style: {
      position: 'absolute',
      zIndex: 1,
      ...children.props.style,
    },
  });
};

const NextButton = ({ children }: PropsWithChildren) => {
  const { slideToNextContent } = useContext(SliderContext)!;

  if (!React.isValidElement(children)) {
    throw new Error('React Element children이 필요합니다');
  }

  return React.cloneElement(children as React.ReactElement, {
    onClick: slideToNextContent,
    style: {
      position: 'absolute',
      zIndex: 1,
      right: 0,
      ...children.props.style,
    },
  });
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
