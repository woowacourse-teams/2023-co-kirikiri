import { BrowserRouter, Route, Routes } from 'react-router-dom';
import theme from '@styles/theme';
import GlobalStyle from '@styles/GlobalStyle';
import { ThemeProvider } from 'styled-components';
import ResponsiveContainer from '@components/_common/responsiveContainer/ResponsiveContainer';
import SignUpPage from '@pages/signUpPage/SignUpPage';
import LoginPage from '@pages/loginPage/LoginPage';

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
                  <p>한글이라네 자네</p>
                  <p>here comes Eng</p>
                </div>
              }
            />
            <Route path='/login' element={<LoginPage />} />
            <Route path='/join' element={<SignUpPage />} />
          </Routes>
        </BrowserRouter>
      </ResponsiveContainer>
    </ThemeProvider>
  );
};

export default App;
