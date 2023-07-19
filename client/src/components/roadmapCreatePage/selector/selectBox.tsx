import { PropsWithChildren, ReactElement } from 'react';
import { useContextScope } from '@/hooks/_common/useContextScope';
import { combineStates, getCustomElement } from '@/hooks/_common/compound';
import {
  DescriptionProps,
  externalStateType,
  IndicatorProps,
  LabelProps,
  OptionGroupProps,
  OptionProps,
  SelectBoxProps,
  SelectContextType,
  TriggerProps,
} from '@/types/_common/select';
import { useSelect } from '@/hooks/_common/useSelect';
import { SelectContext } from '@/context/selectContext';
import { S } from './selectBox.styles';

// select컴포넌트가 context를 공유할 수 있게 하는 provider컴포넌트
export const SelectBox = (
  props: PropsWithChildren<SelectBoxProps<externalStateType>>
) => {
  const { children, defaultOpen, externalSelectState } = props;
  const {
    selectedOption: selectedId,
    selectOption: innerSelectState,
    isSelecBoxOpen,
    toggleBoxOpen,
  } = useSelect<number>(defaultOpen);
  const selectOption = combineStates(externalSelectState, innerSelectState);

  return (
    <SelectContext.Provider
      value={{ isSelecBoxOpen, toggleBoxOpen, selectedId, selectOption }}
    >
      {children}
    </SelectContext.Provider>
  );
};

// select컴포넌트의 라벨
export const Label = (props: PropsWithChildren<LabelProps>) => {
  const { asChild = false, children, ...restProps } = props;

  if (asChild) {
    return getCustomElement(children as ReactElement, { ...restProps });
  }
  return <S.DefaultLabel>{children}</S.DefaultLabel>;
};

// select컴포넌트에 대한 설명
export const Description = (props: PropsWithChildren<DescriptionProps>) => {
  const { asChild = false, children, ...restProps } = props;

  if (asChild) {
    return getCustomElement(children as ReactElement, { ...restProps });
  }
  return <S.DefaultDescription>{children}</S.DefaultDescription>;
};

// 클릭하면 selectBox를 보여줄 수 있는 trigger 버튼
export const Trigger = (props: PropsWithChildren<TriggerProps>) => {
  const { asChild = false, children, ...restProps } = props;
  const { toggleBoxOpen } = useContextScope<SelectContextType>(SelectContext);

  if (asChild) {
    return getCustomElement(children as ReactElement, {
      ...restProps,
      onClick: toggleBoxOpen,
    });
  }
  return <S.DefaultTrigger onClick={toggleBoxOpen}>{children}</S.DefaultTrigger>;
};

// Option들을 담는 컨테이너 컴포넌트
export const OptionGroup = (props: PropsWithChildren<OptionGroupProps>) => {
  const { asChild = false, children, ...restProps } = props;
  const { isSelecBoxOpen } = useContextScope<SelectContextType>(SelectContext);

  if (asChild) {
    return isSelecBoxOpen
      ? getCustomElement(children as ReactElement, { ...restProps })
      : null;
  }
  return isSelecBoxOpen ? <S.DefaultOptionGroup>{children}</S.DefaultOptionGroup> : null;
};

// Option이 선택되었는지 나타내는 indicator
export const Indicator = (props: PropsWithChildren<IndicatorProps>) => {
  const { asChild = false, children, ...restProps } = props;
  const { selectedId } = useContextScope<SelectContextType>(SelectContext);
  const isSelected = restProps.id === selectedId;

  if (asChild) {
    return getCustomElement(children as ReactElement, { ...restProps, isSelected });
  }
  return <S.DefaultIndicator isSelected={isSelected}>{children}</S.DefaultIndicator>;
};

// select의 각 Option
export const Option = (props: PropsWithChildren<OptionProps>) => {
  const { asChild = false, children, ...restProps } = props;
  const { selectOption, selectedId } = useContextScope<SelectContextType>(SelectContext);
  const isSelected = restProps.id === selectedId;

  if (asChild) {
    return getCustomElement(children as ReactElement, {
      ...restProps,
      isSelected,
      onClick: () => selectOption(restProps.id),
    });
  }
  return (
    <S.DefaultOption isSelected={isSelected} onClick={() => selectOption(restProps.id)}>
      {children}
    </S.DefaultOption>
  );
};

export const Select = Object.assign(SelectBox, {
  Label,
  Description,
  Trigger,
  OptionGroup,
  Indicator,
  Option,
});
