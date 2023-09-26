/* eslint-disable @typescript-eslint/no-non-null-assertion */
import React, {
  Dispatch,
  PropsWithChildren,
  SetStateAction,
  createContext,
  useContext,
  useState,
} from 'react';

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
      {children}
    </SliderContext.Provider>
  );
};

const PrevButton = ({ children }: PropsWithChildren) => {
  const { slideToPrevContent } = useContext(SliderContext)!;

  return <button onClick={slideToPrevContent}>{children}</button>;
};

const NextButton = ({ children }: PropsWithChildren) => {
  const { slideToNextContent } = useContext(SliderContext)!;

  return <button onClick={slideToNextContent}>{children}</button>;
};

const Contents = ({ children }: PropsWithChildren) => {
  const { curIndex, setContentLength } = useContext(SliderContext)!;
  const childrenArray = React.Children.toArray(children);
  setContentLength(childrenArray.length);

  return <article>{childrenArray[curIndex]}</article>;
};

Slider.PrevButton = PrevButton;
Slider.NextButton = NextButton;
Slider.Contents = Contents;

export default Slider;
