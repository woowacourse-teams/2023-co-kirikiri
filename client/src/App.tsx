import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { ThemeProvider } from 'styled-components';

import GlobalStyle from '@styles/GlobalStyle';
import theme from '@styles/theme';
import ResponsiveContainer from '@components/@common/responsiveContainer/ResponsiveContainer';

const App = () => {
  return (
    <ThemeProvider theme={theme}>
      <GlobalStyle />
      <ResponsiveContainer>
        <BrowserRouter>
          <Routes>
            <Route
              path='/'
              element={
                <div>
                  <input />
                </div>
              }
            />
          </Routes>
        </BrowserRouter>
      </ResponsiveContainer>
    </ThemeProvider>
  );
};

export default App;
