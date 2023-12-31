import { DialogContext } from '@/context/dialogContext';
import { getCustomElement } from '@/hooks/_common/compound';
import { useContextScope } from '@/hooks/_common/useContextScope';
import { useSwitch } from '@/hooks/_common/useSwitch';
import { DialogBackdropProps, DialogTriggerProps } from '@/myTypes/_common/dialog';
import { PropsWithChildren, ReactElement, useEffect } from 'react';

export const DialogBox = ({
  children,
  defaultOpen = false,
}: PropsWithChildren<{ defaultOpen?: boolean }>) => {
  const {
    isSwitchOn: isOpen,
    turnSwitchOn: openDialog,
    turnSwitchOff: closeDialog,
  } = useSwitch(defaultOpen);

  return (
    <DialogContext.Provider value={{ isOpen, openDialog, closeDialog }}>
      {children}
    </DialogContext.Provider>
  );
};

export const DialogTrigger = (props: PropsWithChildren<DialogTriggerProps>) => {
  const { asChild, children, ...restProps } = props;
  const { isOpen, openDialog, closeDialog } = useContextScope(DialogContext);

  const toggleDialog = () => {
    if (isOpen) closeDialog();
    if (!isOpen) openDialog();
  };

  if (asChild) {
    return getCustomElement(children as ReactElement, {
      ...restProps,
      onClick: toggleDialog,
    });
  }

  return <button onClick={toggleDialog}>Trigger</button>;
};

export const DialogBackdrop = (props: PropsWithChildren<DialogBackdropProps>) => {
  const { asChild = false, children, ...restProps } = props;
  const { isOpen, closeDialog } = useContextScope(DialogContext);

  useEffect(() => {
    document.body.style.overflow = isOpen ? 'hidden' : 'auto';
  }, [isOpen]);

  if (asChild) {
    return isOpen
      ? getCustomElement(children as ReactElement, { ...restProps, onClick: closeDialog })
      : null;
  }

  return isOpen ? <button onClick={closeDialog}>Backdrop</button> : null;
};

export const DialogContent = ({ children }: PropsWithChildren) => {
  const { isOpen } = useContextScope(DialogContext);
  return isOpen ? <>{children}</> : null;
};
