import { PropsWithChildren, useRef, useState } from 'react';
import { createPortal } from 'react-dom';

import * as S from './Toast.styles';
import { ToastContainerProps } from '@myTypes/_common/toast';
import { ToastContext } from '@/context/toastContext';

export const ToastMessage = ({
  message,
  indicator,
  isError = false,
  onClickToast,
}: ToastContainerProps) => {
  return (
    <S.ToastContainer isError={isError} onClick={onClickToast}>
      {indicator}
      <S.ToastMessageContainer>
        <S.ToastMessage>{message}</S.ToastMessage>
      </S.ToastMessageContainer>
    </S.ToastContainer>
  );
};

const ToastProvider = (props: PropsWithChildren) => {
  const { children } = props;

  const timeout = useRef<NodeJS.Timeout | null>(null);

  const [{ message, indicator, isError, isShow }, setMessage] = useState<
    Omit<ToastContainerProps, 'onClickToast'>
  >({
    message: '',
    indicator: null,
    isError: false,
    isShow: null,
  });

  const triggerToast = ({ message, indicator, isError = false }: ToastContainerProps) => {
    if (!timeout.current) {
      setMessage((prev) => ({ ...prev, isError, message, indicator, isShow: true }));

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
                indicator={indicator}
                isError={isError}
                onClickToast={onClickToast}
              />
            </S.ShowUpRoot>
          )}
          {isShow === false && (
            <S.ShowDownRoot>
              <ToastMessage
                message={message}
                indicator={indicator}
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
