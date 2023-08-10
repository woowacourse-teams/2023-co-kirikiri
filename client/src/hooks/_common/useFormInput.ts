import { FormEvent, useState } from 'react';

type FormErrorType = {
  [key: string]: string;
};

type ValidationType = {
  validate: (inputValue: string) => boolean;
  message: string;
  updateOnFail: boolean;
};

type ValidationsType = {
  [key: string]: ValidationType[];
};

const useFormInput = <T extends object>(
  initialState: T,
  validations?: ValidationsType
) => {
  const [formState, setFormState] = useState<T>(initialState);
  const [error, setError] = useState<FormErrorType>();

  const validateInputValue = (name: string, inputValue: string) => {
    if (!validations || !validations?.[name]) return true;

    const shouldUpdateValue = validations[name].every(
      ({ validate, message, updateOnFail }) => {
        if (!validate(inputValue)) {
          setError((prev) => ({
            ...prev,
            [name]: message,
          }));

          return updateOnFail;
        }

        return true;
      }
    );

    return shouldUpdateValue;
  };

  const cleanError = (name: string) => {
    if (!error || !error[name]) return;

    setError((prev) => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const { [name]: _, ...rest } = prev as FormErrorType;

      return rest;
    });
  };

  const handleSubmit = (callback: () => void) => (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!validations) {
      callback();
      return;
    }

    const isFormValid = Object.entries(validations).every(([key, fieldValidations]) =>
      fieldValidations.every(({ validate, message }) => {
        // key 문자열을 구문 분석하여 'parts' 배열 생성
        // ex) "user[details][name]" => ["user", "details", "name"]
        const parts = key.split('[').map((part) => part.replace(']', ''));

        // 중첩된 객체에서 필드 값을 검색
        // "user[details][name]" ex에서 fieldValue는 "name"
        const fieldValue = parts.reduce((currentValue, part) => {
          return (currentValue as any)[part];
        }, formState);

        const isValid = validate(String(fieldValue));

        if (!isValid) {
          setError((prev) => ({
            ...prev,
            [key]: message,
          }));
        }

        return isValid;
      })
    );

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

    const parts = name.split('[').map((part) => part.replace(']', ''));
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
