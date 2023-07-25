import { Suspense } from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import theme from '@styles/theme';
import GlobalStyle from '@styles/GlobalStyle';
import { ThemeProvider } from 'styled-components';
import ResponsiveContainer from '@components/_common/responsiveContainer/ResponsiveContainer';
import SignUpPage from '@pages/signUpPage/SignUpPage';
import LoginPage from '@pages/loginPage/LoginPage';
import RoadmapCreatePage from './pages/roadmapCreatePage/roadmapCreatePage';
import PageLayout from '@components/_common/pageLayout/PageLayout';
import RoadmapListPage from '@pages/roadmapListPage/roadmapListPage';
import GoalRoomListPage from '@components/goalRommListPage/GoalRoomListPage';
import MainPage from '@components/mainPage/MainPage';
import GoalRoomDashboardPage from '@pages/goalRoomDashboardPage/GoalRoomDashboardPage';
import Fallback from '@components/_common/fallback/Fallback';
import RoadmapDetailPage from './pages/roadmapDetailPage/RoadmapDetailPage';

const App = () => {
  return (
    <ThemeProvider theme={theme}>
      <GlobalStyle />
      <BrowserRouter>
        <ResponsiveContainer>
          <PageLayout>
            <Routes>
              <Route path='/' element={<MainPage />} />
              <Route path='/login' element={<LoginPage />} />
              <Route path='/join' element={<SignUpPage />} />
              <Route path='/roadmap-list' element={<RoadmapListPage />} />
              <Route
                path='/roadmap/:id'
                element={
                  <Suspense fallback={<Fallback />}>
                    <RoadmapDetailPage />
                  </Suspense>
                }
              />
              <Route path='/roadmap-create' element={<RoadmapCreatePage />} />
              <Route path='/goalroom-list' element={<GoalRoomListPage />} />
              <Route
                path='/goalroom-dashboard/:goalroomId'
                element={<GoalRoomDashboardPage />}
              />
            </Routes>
          </PageLayout>
        </ResponsiveContainer>
      </BrowserRouter>
    </ThemeProvider>
  );
};

export default App;
