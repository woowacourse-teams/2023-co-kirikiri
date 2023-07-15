import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { ThemeProvider } from 'styled-components';

import GlobalStyle from '@styles/GlobalStyle';
import theme from '@styles/theme';
import ResponsiveContainer from '@components/_common/responsiveContainer/ResponsiveContainer';
import RoadmapCreatePage from './pages/roadmapCreatePage/roadmapCreatePage';

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
            <Route path='/roadmap-create' element={<RoadmapCreatePage />} />
          </Routes>
        </BrowserRouter>
      </ResponsiveContainer>
    </ThemeProvider>
  );
};

export default App;
