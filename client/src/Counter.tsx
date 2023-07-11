import { useState } from 'react';

const Counter = () => {
  const [count, setCount] = useState(0);

  const increaseCount = () => {
    setCount((prev) => prev + 1);
  };

  return (
    <div>
      <div>{count}</div>
      <button onClick={increaseCount}>ok</button>
    </div>
  );
};

export default Counter;
