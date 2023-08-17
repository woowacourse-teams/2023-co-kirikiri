import { DefaultTheme } from 'styled-components';
import font from '@styles/font';

const theme: DefaultTheme = {
  colors: {
    main_light: '#C8EE44',
    main_middle: '#99c799',
    main_dark: '#76A082',
    gray_back: '#f8f8fa',
    gray_light: '#fbfbfb',
    gray100: '#F2F2F2',
    gray200: '#C7C7CA',
    gray300: '#9E9EAE',
    black: '#000',
    white: '#fff',
    red: '#ff0000',
    backdrop: 'rgba(220, 220, 220, 0.44)',
    transparent_blue: 'rgba(0, 100, 250, 0.1)',
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
    description5: font({ size: 1.8, weight: 400 }),
    caption1: font({ size: 1.05, weight: 400 }),
    body1: font({ size: 1.8, weight: 300 }),
  },

  zIndex: {
    navBar: 1,
    header: 2,
    navBarOverlay: 3,
    toast: 4,
  },

  shadows: {
    box: '-0.14rem 0.7rem 4.3rem rgba(0, 0, 0, 0.13)',
    text: '0 0.2rem 0.2rem rgba(0, 0, 0, 0.18)',
    main: 'rgba(0, 0, 0, 0.25) 0px 0px 0.315rem',
    threeD: '0 1px 3px rgba(0, 0, 0, 0.12), 0 1px 2px rgba(0, 0, 0, 0.24)',
    threeDHovered: '0 14px 28px rgba(0, 0, 0, 0.25), 0 10px 10px rgba(0, 0, 0, 0.22)',
    modal: '0px 5px 15px rgba(0, 0, 0, 0.2)',
  },
};

export default theme;
