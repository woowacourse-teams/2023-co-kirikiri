import React from 'react';
import ReactDOM from 'react-dom/client';
import theme from '@styles/theme';
import GlobalStyle from '@styles/GlobalStyle';
import ResponsiveContainer from '@components/_common/responsiveContainer/ResponsiveContainer';
import { ThemeProvider } from 'styled-components';
import App from './App';

const rootElement = document.getElementById('root');
if (!rootElement) throw new Error('Failed to find the root element');
const root = ReactDOM.createRoot(rootElement);

root.render(
  <React.StrictMode>
    <ThemeProvider theme={theme}>
      <GlobalStyle />
      <ResponsiveContainer>
        <App />
      </ResponsiveContainer>
    </ThemeProvider>
  </React.StrictMode>
);
