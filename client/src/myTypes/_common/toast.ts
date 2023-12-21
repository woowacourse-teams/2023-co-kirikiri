export type ToastContainerProps = {
  message: string;
  indicator: JSX.Element | null;
  isError?: boolean;
  isShow?: boolean | null;
  onClickToast?: () => void;
};

export type ToastContextType = {
  triggerToast: (arg: ToastContainerProps) => void;
};
