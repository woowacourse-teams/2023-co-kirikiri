import { useState } from 'react';

const useFormInput = <T extends object>(initialState: T) => {
  const [formState, setFormState] = useState<T>(initialState);

  const handleInputChange = (
    event: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    setFormState({
      ...formState,
      [event.target.name]: event.target.value,
    });
  };

  const resetFormState = () => {
    setFormState(initialState);
  };

  return {
    formState,
    handleInputChange,
    resetFormState,
  };
};

export default useFormInput;
