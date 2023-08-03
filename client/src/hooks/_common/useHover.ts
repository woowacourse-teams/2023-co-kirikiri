import { useState, MouseEventHandler } from 'react';

/**
 * hover 시 상태를 관리하는 hook
 * @returns {isHovered, handleMouseEnter, handleMouseLeave}
 */

type UseHover = {
  isHovered: boolean;
  handleMouseEnter: MouseEventHandler;
  handleMouseLeave: MouseEventHandler;
};

const useHover = (): UseHover => {
  const [isHovered, setIsHovered] = useState(false);

  const handleMouseEnter = () => setIsHovered(true);
  const handleMouseLeave = () => setIsHovered(false);

  return { isHovered, handleMouseEnter, handleMouseLeave };
};

export default useHover;
