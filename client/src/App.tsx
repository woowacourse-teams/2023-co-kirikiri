import { Suspense } from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import theme from '@styles/theme';
import GlobalStyle from '@styles/GlobalStyle';
import { ThemeProvider } from 'styled-components';
import ResponsiveContainer from '@components/_common/responsiveContainer/ResponsiveContainer';
import SignUpPage from '@pages/signUpPage/SignUpPage';
import LoginPage from '@pages/loginPage/LoginPage';
import PageLayout from '@components/_common/pageLayout/PageLayout';
import RoadmapListPage from '@pages/roadmapListPage/roadmapListPage';
import GoalRoomDashboardPage from '@pages/goalRoomDashboardPage/GoalRoomDashboardPage';
import Fallback from '@components/_common/fallback/Fallback';
import RoadmapDetailPage from './pages/roadmapDetailPage/RoadmapDetailPage';
import RoadmapCreatePage from './pages/roadmapCreatePage/RoadmapCreatePage';
import ToastProvider from '@components/_common/toastProvider/ToastProvider';
import GoalRoomListPage from './pages/goalRoomListPage/GoalRoomListPage';
import GoalRoomCreatePage from './pages/goalRoomCreatePage/GoalRoomCreatePage';
import MyPage from '@pages/myPage/MyPage';
import UserInfoProvider from './components/_providers/UserInfoProvider';
import RoadmapSearchResult from './components/roadmapListPage/roadmapSearch/RoadmapSearchResult';

const App = () => {
  return (
    <ThemeProvider theme={theme}>
      <GlobalStyle />
      <UserInfoProvider>
        <ToastProvider>
          <BrowserRouter>
            <ResponsiveContainer>
              <PageLayout>
                <Routes>
                  <Route path='/' element={<RoadmapListPage />} />
                  <Route path='/login' element={<LoginPage />} />
                  <Route path='/join' element={<SignUpPage />} />
                  <Route path='/roadmap-list' element={<RoadmapListPage />}>
                    <Route path=':category/:search' element={<RoadmapSearchResult />} />
                  </Route>
                  <Route
                    path='/roadmap/:id'
                    element={
                      <Suspense fallback={<Fallback />}>
                        <RoadmapDetailPage />
                      </Suspense>
                    }
                  />
                  <Route
                    path='/roadmap/:id/goalroom-list'
                    element={<GoalRoomListPage />}
                  />
                  <Route path='/roadmap-create' element={<RoadmapCreatePage />} />
                  <Route
                    path='/roadmap/:id/goalroom-create'
                    element={<GoalRoomCreatePage />}
                  />
                  <Route
                    path='/goalroom-dashboard/:goalroomId'
                    element={<GoalRoomDashboardPage />}
                  />
                  <Route path='/myPage' element={<MyPage />} />
                </Routes>
              </PageLayout>
            </ResponsiveContainer>
          </BrowserRouter>
        </ToastProvider>
      </UserInfoProvider>
    </ThemeProvider>
  );
};

export default App;
