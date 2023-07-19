import { BrowserRouter, Route, Routes } from 'react-router-dom';
import theme from '@styles/theme';
import GlobalStyle from '@styles/GlobalStyle';
import { ThemeProvider } from 'styled-components';
import ResponsiveContainer from '@components/_common/responsiveContainer/ResponsiveContainer';
import SignUpPage from '@pages/signUpPage/SignUpPage';
import LoginPage from '@pages/loginPage/LoginPage';
import PageLayout from '@components/_common/pageLayout/PageLayout';
import RoadmapCreatePage from './pages/roadmapCreatePage/roadmapCreatePage';

const App = () => {
  return (
    <ThemeProvider theme={theme}>
      <GlobalStyle />
      <ResponsiveContainer>
        <PageLayout>
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
              <Route path='/roadmap-create' element={<RoadmapCreatePage />} />
              <Route path='/login' element={<LoginPage />} />
              <Route path='/join' element={<SignUpPage />} />
            </Routes>
          </BrowserRouter>
        </PageLayout>
      </ResponsiveContainer>
    </ThemeProvider>
  );
};

export default App;
