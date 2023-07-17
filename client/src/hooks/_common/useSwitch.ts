import { useState } from 'react';

export const useSwitch = (initState?: boolean) => {
  const [isSwitchOn, setIsSwitchOn] = useState(Boolean(initState));

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
