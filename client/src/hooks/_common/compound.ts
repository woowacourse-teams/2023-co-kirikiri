import { Attributes, Children, cloneElement, isValidElement, ReactElement } from 'react';
import { CombineStateType } from '@/myTypes/_common/compound';

export const combineStates: CombineStateType = (externalState, innerState) => {
  return (params) => {
    externalState?.(params);
    innerState?.(params);
  };
};

const getValidChild = (children: ReactElement) => {
  return Children.only(children);
};

const validateCustomChildren = (children: ReactElement) => {
  return isValidElement(children);
};

export const getCustomElement = <P extends Partial<P> & Attributes>(
  children: ReactElement,
  props: P
) => {
  if (validateCustomChildren(children)) {
    return cloneElement(getValidChild(children), props);
  }
  throw Error('Invalid React Element!');
};
