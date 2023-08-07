import { useState } from 'react';

const useFormInput = <T extends object>(initialState: T) => {
  const [formState, setFormState] = useState<T>(initialState);

  const handleInputChange = ({
    target: { name, value },
  }: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    // 이름을 '['를 기준으로 분리하고, ']'를 제거
    const parts = name.split('[').map((part) => part.replace(']', ''));
    // 배열 요소인지 확인하기 위해 첫 번째 요소가 'goalRoomRoadmapNodeRequests'인지 확인
    const isArray = parts.length > 2;

    // 배열 요소일 때. 즉, NodeList 내부의 값이 변했을 때
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
      // 배열 요소가 아닐 때
      const [propName, nestedPropName] = parts;
      setFormState((prevState: any) => {
        // 객체 내부의 객체를 업데이트 하기 위함 (2 Depth)
        if (nestedPropName) {
          return {
            ...prevState,
            [propName]: {
              ...prevState[propName],
              [nestedPropName]: value,
            },
          };
        }
        // 속성을 업데이트 (1 Depth)
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
  };
};

export default useFormInput;
