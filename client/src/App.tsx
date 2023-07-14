import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { useFetchUser } from '@hooks/queries/user';
import { useEffect } from 'react';
import theme from '@styles/theme';
import GlobalStyle from '@styles/GlobalStyle';
import { ThemeProvider } from 'styled-components';
import ResponsiveContainer from '@components/_common/responsiveContainer/ResponsiveContainer';

const App = () => {
  const { user } = useFetchUser();

  useEffect(() => {
    console.log(user, 'userInfo');
  }, [user]);

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
                  <p>한글이라네 자네</p>
                  <p>here comes Eng</p>
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
