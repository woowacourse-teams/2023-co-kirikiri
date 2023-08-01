import { ToastContext } from '@components/_common/toastProvider/ToastProvider';
import { useContext } from 'react';

const useToast = () => {
  const { triggerToast } = useContext(ToastContext);

  return {
    triggerToast,
  };
};

export default useToast;
