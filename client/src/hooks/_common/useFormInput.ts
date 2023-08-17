import { ChangeEvent, FormEvent, useState } from 'react';

export type FormErrorType = {
  [key: string]: string;
};

export type ValidationReturnType = {
  ok: boolean;
  message?: string;
  updateOnFail?: boolean;
};

export type ValidationFunctionType = (
  inputValue: string,
  formState?: any
) => ValidationReturnType;

export type ValidationsType = {
  [key: string]: ValidationFunctionType;
};

export type HandleInputChangeType = (
  e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
) => void;

export type ObjectType = Record<string, any>;

const getParts = (path: string) => {
  return path.split('[').map((part) => part.replace(']', ''));
};

const getNestedValue = (obj: ObjectType, path: string) => {
  if (obj === null) return obj;

  const parts = getParts(path);
  return parts.reduce((curr, part) => curr[part], obj);
};

const setNestedValue = <T extends ObjectType>(obj: T, path: string, value: unknown) => {
  if (obj === null) return obj;

  const parts = getParts(path);
  const lastKey = parts.pop();

  if (!lastKey) {
    return obj;
  }

  const lastObj = parts.reduce((curr, part) => curr[part], obj);
  (lastObj as ObjectType)[lastKey] = value;

  return obj;
};

const useFormInput = <T extends object>(
  initialState: T,
  validations?: ValidationsType
) => {
  const [formState, setFormState] = useState<T>(initialState);
  const [error, setError] = useState<FormErrorType>({});

  const validateInputValue = (name: string, inputValue: string) => {
    if (typeof validations?.[name] !== 'function') return true;

    const result = validations[name](inputValue, formState);

    if (!result.ok) {
      setError((prev) => ({
        ...prev,
        [name]: result.message || '',
      }));
    }

    return result.ok || result.updateOnFail;
  };

  const cleanError = (name: string) => {
    if (!error[name]) return;

    setError((prev) => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const { [name]: _, ...rest } = prev;
      return rest;
    });
  };

  const updateFormState = (name: string, value: unknown) => {
    setFormState((prev) => setNestedValue({ ...prev }, name, value));
  };

  const handleInputChange = ({
    target: { name, value },
  }: ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    cleanError(name);

    if (validateInputValue(name, value)) {
      updateFormState(name, value);
    }
  };

  const isFormValid = () => {
    let isValid = true;
    if (!validations) return isValid;

    Object.keys(validations).forEach((key) => {
      const result = validations[key](String(getNestedValue(formState, key)), formState);

      if (!result.ok) {
        setError((prev) => ({
          ...prev,
          [key]: result.message || '',
        }));
        isValid = false;
      }
    });

    return isValid;
  };

  const handleSubmit = (callback: () => void) => (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (isFormValid()) callback();
  };

  const resetFormState = () => {
    setFormState(initialState);
  };

  return {
    formState,
    handleInputChange,
    resetFormState,
    error,
    handleSubmit,
  };
};

export default useFormInput;
