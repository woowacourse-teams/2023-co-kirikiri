import { css } from 'styled-components';

interface Font {
  size: number;
  weight: number;
  lineHeight: number;
}

/*
    Usage:

    import font from '@styles/fonts';

    const StyledComponent = styled.div`
        ${font({
            size: 1.6,
            weight: 700,
               lineHeight: 2.4,
        })}
    `;
 */
const font = ({ size, weight, lineHeight }: Font) => {
  return css`
    font-size: ${size}rem;
    font-weight: ${weight};
    line-height: ${lineHeight}rem;
  `;
};

export default font;
