import { ToastContext } from '@/context/toastContext';
import { useContext } from 'react';

const useToast = () => {
  const { triggerToast } = useContext(ToastContext);

  return {
    triggerToast,
  };
};

export default useToast;
