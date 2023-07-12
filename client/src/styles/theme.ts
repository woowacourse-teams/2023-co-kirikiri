import { DefaultTheme } from 'styled-components';

declare module 'styled-components' {
  export interface DefaultTheme {
    colors: {
      [key: string]: string;
    };
  }
}

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
};

export default theme;
