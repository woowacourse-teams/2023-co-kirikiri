export type DialogContextType = {
  isOpen: boolean;
  openDialog: () => void;
  closeDialog: () => void;
};

export type DialogTriggerProps = {
  asChild?: boolean;
};

export type DialogBackdropProps = {
  asChild?: boolean;
};
