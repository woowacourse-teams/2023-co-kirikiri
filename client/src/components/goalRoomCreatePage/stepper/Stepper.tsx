import { useState } from 'react';
import * as S from './Stepper.styles';

type StepperProps = {
  initState?: number;
};

/* TODO 
useCkForm에서 setValue 기능을 구현한 후, 
count input 추가 및 Stepper 컴포넌트 내부의 로직 제거 예정
*/
export const Stepper = ({ initState = 1 }: StepperProps) => {
  const [count, setCount] = useState(initState);

  const increaseCount = () => {
    setCount((prev) => prev + 1);
  };

  const decreaseCount = () => {
    if (count === 1) return;

    setCount((prev) => prev - 1);
  };

  return (
    <S.Stepper>
      <S.Button type='button' onClick={decreaseCount}>
        -
      </S.Button>
      <S.Count>{count}</S.Count>
      <S.Button type='button' onClick={increaseCount}>
        +
      </S.Button>
    </S.Stepper>
  );
};
