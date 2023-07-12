import { css } from 'styled-components';
import BREAK_POINTS from '@constants/common/breakPoints';

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
    @media (max-width: ${BREAK_POINTS.MOBILE}) {
      ${styles}
    }
  `,

  desktop: (styles: TemplateStringsArray) => css`
    @media (max-width: ${BREAK_POINTS.DESKTOP}) {
      ${styles}
    }
  `,
};

export default media;
