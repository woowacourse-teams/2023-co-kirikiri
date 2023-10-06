import { createGlobalStyle } from 'styled-components';
import BREAK_POINTS from '@constants/_common/breakPoints';

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

  li {
    list-style: none;
  }

  button, input, select {
    margin: 0;
    background-color: transparent;
    border: none;
  }

  *, :after, :before {
    box-sizing: inherit;
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

  /* global styles */

  @font-face {
    font-family: 'Noto Sans KR';
    font-display: optional;
    src: url(${require('../assets/fonts/NotoSansKR-Regular.woff')}) format('woff');
    unicode-range: U+0020-007E;
  }

  @font-face {
    font-family: 'Noto Sans';
    font-display: optional;
    src: url(${require('../assets/fonts/NotoSans-Regular.woff')}) format('woff');
    unicode-range: U+0020-007E;
  }

  :root {
    font-size: 41.6%; /* for mobile devices */
    background: ${({ theme }) => theme.colors.gray_back};
    background: ${({ theme }) => theme.colors.gray_back};

    @media (max-width: ${BREAK_POINTS.MOBILE}px) {
      font-size: 47.5%; /* for tablet devices */
    }

    @media (max-width: ${BREAK_POINTS.TABLET}px) {
      font-size: 55%; /* for tablet devices */
    }

    @media (min-width: ${BREAK_POINTS.DESKTOP}px) {
      font-size: 62.5%; /* for desktop devices */
    }
  }

  * {
    scrollbar-width: none;
    font-family: 'Noto Sans KR', 'Noto Sans' , sans-serif;
    white-space: pre-wrap;


    &::-webkit-scrollbar {
      display: none; /* 크롬, 사파리, 오페라, 엣지 */
    }
  }

  input {
    padding:0 ;
    border: none;
    outline: none;
  }

  textarea {
    resize: none;
    background-color: transparent;
    border: none;
    outline: none;
  }

  abbr {
    text-decoration: none;
  }
  
  button, select {
    cursor: pointer;
  }
  
  a {
    text-decoration: none;
  }
  
  input, select {
    background: inherit;
  }
`;

export default GlobalStyle;
