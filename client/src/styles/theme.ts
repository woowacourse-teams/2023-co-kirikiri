import { DefaultTheme } from 'styled-components';
import font from '@styles/font';

const theme: DefaultTheme = {
  colors: {
    main_light: '#C8EE44',
    main_dark: '#76A082',
    gray100: '#F2F2F2',
    gray200: '#F5F5F5',
    gray300: '#9E9EAE',
    black: '#000',
    white: '#fff',
    red: '#ff0000',
  },

  fonts: {
    nav_title: font({ size: 1.5, weight: 700, lineHeight: 2.18 }),
    nav_text: font({ size: 1, weight: 400, lineHeight: 1.4 }),
    title_large: font({ size: 1.75, weight: 700, lineHeight: 1.2 }),
    h1: font({ size: 1.25, weight: 700, lineHeight: 0.86 }),
    h2: font({ size: 1.06, weight: 700, lineHeight: 1.2 }),
    button1: font({ size: 0.87, weight: 700, lineHeight: 1.2 }),
    button2: font({ size: 1.06, weight: 400, lineHeight: 1.2 }),
    button3: font({ size: 1.75, weight: 400, lineHeight: 1.2 }),
    description1: font({ size: 0.62, weight: 400, lineHeight: 1.2 }),
    description2: font({ size: 0.75, weight: 300, lineHeight: 1.2 }),
    description3: font({ size: 0.68, weight: 700, lineHeight: 1.2 }),
    description4: font({ size: 0.75, weight: 400, lineHeight: 1.2 }),
    description5: font({ size: 1.22, weight: 300, lineHeight: 1.2 }),
    caption1: font({ size: 0.65, weight: 400, lineHeight: 1.2 }),
    body1: font({ size: 1.12, weight: 300, lineHeight: 1.2 }),
  },

  shadows: {
    box: '-1.4px 7px 42.4px rgba(0, 0, 0, 0.13)',
    text: '0px 3px 3px rgba(0, 0, 0, 0.2)',
  },
};

export default theme;
