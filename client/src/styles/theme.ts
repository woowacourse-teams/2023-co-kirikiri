import { DefaultTheme } from 'styled-components';
import font from '@styles/font';

const theme: DefaultTheme = {
  colors: {
    main_light: '#C8EE44',
    main_dark: '#76A082',
    gray100: '#F2F2F2',
    gray200: '#C7C7CA',
    gray300: '#9E9EAE',
    black: '#000',
    white: '#fff',
    red: '#ff0000',
  },

  fonts: {
    nav_title: font({ size: 2.5, weight: 700 }),
    nav_text: font({ size: 1.6, weight: 400 }),
    title_large: font({ size: 2.8, weight: 700 }),
    h1: font({ size: 2, weight: 700 }),
    h2: font({ size: 1.7, weight: 700 }),
    button1: font({ size: 1.4, weight: 700 }),
    button2: font({ size: 1.7, weight: 400 }),
    button3: font({ size: 2.8, weight: 400 }),
    description1: font({ size: 1, weight: 400 }),
    description2: font({ size: 1.2, weight: 300 }),
    description3: font({ size: 1.1, weight: 700 }),
    description4: font({ size: 1.2, weight: 400 }),
    description5: font({ size: 1.96, weight: 300 }),
    caption1: font({ size: 1.05, weight: 400 }),
    body1: font({ size: 1.8, weight: 300 }),
  },

  zIndex: {
    navBar: 1,
    header: 2,
    navBarOverlay: 3,
  },

  shadows: {
    box: '-0.14rem 0.7rem 4.3rem rgba(0, 0, 0, 0.13)',
    text: '0 0.2rem 0.2rem rgba(0, 0, 0, 0.18)',
  },
};

export default theme;
