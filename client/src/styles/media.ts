import { css } from 'styled-components';
import BREAK_POINTS from '@constants/_common/breakPoints';

/*
    Usage:

    import media from '@styles/media';

    const StyledComponent = styled.div`
        ${media.mobile`
            background-color: red;
        `}
        ${media.desktop`
            background-color: blue;
        `}
    `;
 */

const media = {
  mobile: (styles: TemplateStringsArray) => css`
    @media (max-width: ${BREAK_POINTS.MOBILE}px) {
      ${styles}
    }
  `,

  desktop: (styles: TemplateStringsArray) => css`
    @media (max-width: ${BREAK_POINTS.DESKTOP}px) {
      ${styles}
    }
  `,
};

export default media;
