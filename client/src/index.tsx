import React from 'react';
import ReactDOM from 'react-dom/client';
import theme from '@styles/theme';
import GlobalStyle from '@styles/GlobalStyle';
import ResponsiveContainer from '@components/_common/responsiveContainer/ResponsiveContainer';
import { ThemeProvider } from 'styled-components';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import App from './App';

const rootElement = document.getElementById('root');
if (!rootElement) throw new Error('Failed to find the root element');
const root = ReactDOM.createRoot(rootElement);

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      suspense: true,
      retry: false,
      useErrorBoundary: true,
      refetchOnWindowFocus: false,
      staleTime: 1000 * 60 * 5,
      cacheTime: 1000 * 60 * 30,
    },
    mutations: {
      useErrorBoundary: true,
    },
  },
});

root.render(
  <React.StrictMode>
    <QueryClientProvider client={queryClient}>
      <ReactQueryDevtools initialIsOpen={false} />
      <ThemeProvider theme={theme}>
        <GlobalStyle />
        <ResponsiveContainer>
          <App />
        </ResponsiveContainer>
      </ThemeProvider>
    </QueryClientProvider>
  </React.StrictMode>
);
