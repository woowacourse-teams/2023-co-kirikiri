export type SelectContextType = {
  isSelecBoxOpen: boolean;
  toggleBoxOpen: () => void;
  selectedId: number | null;
  selectOption: (id: number) => void;
};

export type SelectBoxProps<T> = {
  defaultOpen?: boolean;
  externalSelectState?: T;
};

export type externalStateType = {
  (id: number): void;
};

export type LabelProps = {
  asChild: boolean;
};

export type DescriptionProps = {
  asChild: boolean;
};

export type TriggerProps = {
  asChild: boolean;
};

export type OptionGroupProps = {
  asChild: boolean;
};

export type OptionProps = {
  asChild: boolean;
  id: number;
};

export type IndicatorProps = OptionProps;
