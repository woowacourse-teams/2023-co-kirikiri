export type ToastContainerProps = {
  message: string;
  isError?: boolean;
  isShow?: boolean | null;
  onClickToast?: () => void;
};

export type ToastContextType = {
  triggerToast: (arg: ToastContainerProps) => void;
};
