import { createContext, PropsWithChildren, useRef, useState } from 'react';
import { createPortal } from 'react-dom';
import SVGIcon from '@components/icons/SVGIcon';

import * as S from './ToastProvider.styles';
import { ToastContainerProps, ToastContextType } from '@myTypes/_common/toast';

export const ToastMessage = ({
  isError = false,
  message,
  onClickToast,
}: ToastContainerProps) => {
  return (
    <S.ToastContainer isError={isError} onClick={onClickToast}>
      <S.ToastMessageContainer>
        <S.ToastMessage>{message}</S.ToastMessage>
        <SVGIcon name='BackArrowIcon' />
      </S.ToastMessageContainer>
    </S.ToastContainer>
  );
};

export const ToastContext = createContext<ToastContextType>({
  triggerToast: () => {},
});

const ToastProvider = (props: PropsWithChildren) => {
  const { children } = props;

  const timeout = useRef<NodeJS.Timeout | null>(null);

  const [{ message, isError, isShow }, setMessage] = useState<
    Omit<ToastContainerProps, 'onClickToast'>
  >({
    message: '',
    isError: false,
    isShow: null,
  });

  const triggerToast = ({ message, isError = false }: ToastContainerProps) => {
    if (!timeout.current) {
      setMessage((prev) => ({ ...prev, isError, message, isShow: true }));

      timeout.current = setTimeout(() => {
        setMessage((prev) => ({ ...prev, isShow: false }));
      }, 2500);

      return;
    }

    if (timeout.current) {
      clearTimeout(timeout.current);
      timeout.current = null;

      setMessage((prev) => ({ ...prev, isShow: false }));

      requestAnimationFrame(() => {
        setMessage((prev) => ({
          ...prev,
          isError,
          message,
          isShow: true,
        }));

        timeout.current = setTimeout(() => {
          setMessage((prev) => ({ ...prev, isShow: false }));
        }, 1500);
      });
    }
  };

  const onClickToast = () => {
    setMessage((prev) => ({ ...prev, isShow: false }));

    if (timeout.current) {
      clearTimeout(timeout.current);
      timeout.current = null;
    }
  };

  return (
    <ToastContext.Provider value={{ triggerToast }}>
      {children}
      {createPortal(
        <>
          {isShow === true && (
            <S.ShowUpRoot role='status' aria-live='polite'>
              <ToastMessage
                message={message}
                isError={isError}
                onClickToast={onClickToast}
              />
            </S.ShowUpRoot>
          )}
          {isShow === false && (
            <S.ShowDownRoot>
              <ToastMessage
                message={message}
                isError={isError}
                onClickToast={onClickToast}
              />
            </S.ShowDownRoot>
          )}
        </>,
        document.querySelector('#root') as Element
      )}
    </ToastContext.Provider>
  );
};

export default ToastProvider;
