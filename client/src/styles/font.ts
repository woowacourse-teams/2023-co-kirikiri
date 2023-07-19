import { css } from 'styled-components';

interface Font {
  size: number;
  weight: number;
}

/*
    Usage:

    import font from '@styles/fonts';

    const StyledComponent = styled.div`
        ${font({
            size: 1.6,
            weight: 700,
        })}
    `;
 */
const font = ({ size, weight }: Font) => {
  return css`
    font-size: ${size}rem;
    font-weight: ${weight};
  `;
};

export default font;
