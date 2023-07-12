import { createGlobalStyle } from 'styled-components';
import BREAK_POINTS from '@constants/breakPoints';

const GlobalStyle = createGlobalStyle`
  /*! minireset.css v0.0.6 | MIT License | github.com/jgthms/minireset.css */
  html {
    box-sizing: border-box
  }

  blockquote, body, dd, dl, dt, fieldset, figure, h1, h2, h3, h4, h5, h6, hr, html, iframe, legend, li, ol, p, pre, textarea, ul {
    margin: 0;
    padding: 0
  }

  h1, h2, h3, h4, h5, h6 {
    font-size: 100%;
    font-weight: 400
  }

  ul {
    list-style: none
  }

  button, input, select {
    margin: 0
  }

  *, :after, :before {
    box-sizing: inherit
  }

  img, video {
    max-width: 100%;
    height: auto
  }

  iframe {
    border: 0
  }


  table {
    border-spacing: 0;
    border-collapse: collapse
  }

  td, th {
    padding: 0
  }

  :root {
    font-size: 24px; /* for mobile devices */

    @media (min-width: ${BREAK_POINTS.TABLET}px) {
      font-size: 18px; /* for tablet devices */
    }

    @media (min-width: ${BREAK_POINTS.DESKTOP}px) {
      font-size: 16px; /* for desktop devices */
    }
  }
`;

export default GlobalStyle;
