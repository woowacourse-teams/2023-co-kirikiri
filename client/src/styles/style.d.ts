import 'styled-components';

declare module 'styled-components' {
  export interface DefaultTheme {
    colors: {
      [key: string]: string;
    };
    fonts: {
      [key: string]: RuleSet<object>;
    };
    shadows: {
      [key: string]: string;
    };
  }
}
