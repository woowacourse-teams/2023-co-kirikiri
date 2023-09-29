import { Dispatch, PropsWithChildren, SetStateAction } from 'react';

export type SliderContextType = {
  curIndex: number;
  slideToPrevContent: () => void;
  slideToNextContent: () => void;
  setCurIndex: Dispatch<SetStateAction<number>>;
  setContentLength: Dispatch<SetStateAction<number>>;
  isHovered: boolean;
  isFirstContentIndex: boolean;
  isLastContentIndex: boolean;
};

type ButtonProps = {
  width: number;
  height: number;
} & PropsWithChildren;

export type PrevButtonProps = {
  left: number;
} & ButtonProps;

export type NextButtonProps = {
  right: number;
} & ButtonProps;
