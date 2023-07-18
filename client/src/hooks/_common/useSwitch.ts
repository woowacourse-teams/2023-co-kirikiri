import { useState } from 'react';

export const useSwitch = (initState = false) => {
  const [isSwitchOn, setIsSwitchOn] = useState(initState);

  const turnSwitchOn = () => {
    setIsSwitchOn(true);
  };

  const turnSwitchOff = () => {
    setIsSwitchOn(false);
  };

  const toggleSwitch = () => {
    setIsSwitchOn((prev) => !prev);
  };

  return { isSwitchOn, turnSwitchOn, turnSwitchOff, toggleSwitch };
};
