import { FormEvent, useState } from 'react';

export type FormErrorType = {
  [key: string]: string;
};

export type ValidationReturnType = {
  ok: boolean;
  message?: string;
  updateOnFail?: boolean;
};

export type ValidationFunctionType = (inputValue: string) => ValidationReturnType;

export type ValidationsType = {
  [key: string]: ValidationFunctionType;
};

export type HandleInputChangeType = (
  e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
) => void;

const getParts = (path: string) => {
  return path.split('[').map((part) => part.replace(']', ''));
};

const getNestedValue = (obj: any, path: string) => {
  const parts = getParts(path);
  return parts.reduce((curr, part) => curr[part], obj);
};

const useFormInput = <T extends object>(
  initialState: T,
  validations?: ValidationsType
) => {
  const [formState, setFormState] = useState<T>(initialState);
  const [error, setError] = useState<FormErrorType>({});

  const validateInputValue = (name: string, inputValue: string) => {
    if (typeof validations?.[name] !== 'function') return true;

    const result = validations[name](inputValue);

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

  const handleSubmit = (callback: () => void) => (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!validations) {
      callback();
      return;
    }

    let isFormValid = true;

    Object.entries(validations).forEach(([key, fieldValidation]) => {
      const fieldValue = getNestedValue(formState, key);

      const result = fieldValidation(String(fieldValue));

      if (!result.ok) {
        setError((prev) => ({
          ...prev,
          [key]: result.message || '',
        }));

        isFormValid = false;
      }
    });

    if (isFormValid) {
      callback();
    }
  };

  const handleInputChange = ({
    target: { name, value },
  }: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    cleanError(name);

    const shouldUpdateValue = validateInputValue(name, value);
    if (!shouldUpdateValue) return;

    const parts = getParts(name);
    const isArray = parts.length > 2;

    if (isArray) {
      const [baseName, arrayIndex, arrayPropName] = parts;

      setFormState((prevState: any) => {
        if (Array.isArray(prevState[baseName])) {
          return {
            ...prevState,
            [baseName]: prevState[baseName].map((item: any, index: number) => {
              if (index === Number(arrayIndex)) {
                return {
                  ...item,
                  [arrayPropName]: value,
                };
              }
              return item;
            }),
          };
        }
        return prevState;
      });
    } else {
      const [propName, nestedPropName] = parts;
      setFormState((prevState: any) => {
        if (nestedPropName) {
          return {
            ...prevState,
            [propName]: {
              ...prevState[propName],
              [nestedPropName]: value,
            },
          };
        }
        return {
          ...prevState,
          [propName]: value,
        };
      });
    }
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
