import { lazy, Suspense } from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import theme from '@styles/theme';
import GlobalStyle from '@styles/GlobalStyle';
import { ThemeProvider } from 'styled-components';
import ResponsiveContainer from '@components/_common/responsiveContainer/ResponsiveContainer';
import SignUpPage from '@pages/signUpPage/SignUpPage';
import LoginPage from '@pages/loginPage/LoginPage';
import PageLayout from '@components/_common/pageLayout/PageLayout';
import RoadmapListPage from '@pages/roadmapListPage/roadmapListPage';
import Fallback from '@components/_common/fallback/Fallback';
import RoadmapDetailPage from './pages/roadmapDetailPage/RoadmapDetailPage';
import RoadmapCreatePage from './pages/roadmapCreatePage/RoadmapCreatePage';
import ToastProvider from '@components/_common/toastProvider/ToastProvider';
import MyPage from '@pages/myPage/MyPage';
import UserInfoProvider from './components/_providers/UserInfoProvider';
import RoadmapSearchResult from './components/roadmapListPage/roadmapSearch/RoadmapSearchResult';
import MainPage from '@pages/mainPage/MainPage';
import OAuthRedirect from './components/loginPage/OAuthRedirect';
import AsyncBoundary from './components/_common/errorBoundary/AsyncBoundary';
import SessionHandler from '@components/_common/sessionHandler/SessionHandler';
import PrivateRouter from '@components/_common/privateRouter/PrivateRouter';

const GoalRoomDashboardPage = lazy(
  () => import('@pages/goalRoomDashboardPage/GoalRoomDashboardPage')
);
const GoalRoomListPage = lazy(() => import('@pages/goalRoomListPage/GoalRoomListPage'));
const GoalRoomCreatePage = lazy(
  () => import('@pages/goalRoomCreatePage/GoalRoomCreatePage')
);

const App = () => {
  return (
    <ThemeProvider theme={theme}>
      <GlobalStyle />
      <UserInfoProvider>
        <ToastProvider>
          <BrowserRouter>
            <ResponsiveContainer>
              <PageLayout>
                <AsyncBoundary>
                  <SessionHandler>
                    <Routes>
                      <Route path='/' element={<MainPage />} />
                      <Route path='/login' element={<LoginPage />} />
                      <Route path='/join' element={<SignUpPage />} />
                      <Route path='/roadmap-list' element={<RoadmapListPage />}>
                        <Route
                          path=':category/:search'
                          element={<RoadmapSearchResult />}
                        />
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
                      <Route
                        path='/roadmap-create'
                        element={
                          <PrivateRouter>
                            <RoadmapCreatePage />
                          </PrivateRouter>
                        }
                      />
                      <Route
                        path='/roadmap/:id/goalroom-create'
                        element={
                          <PrivateRouter>
                            <GoalRoomCreatePage />
                          </PrivateRouter>
                        }
                      />
                      <Route
                        path='/goalroom-dashboard/:goalroomId'
                        element={<GoalRoomDashboardPage />}
                      />
                      <Route
                        path='/myPage'
                        element={
                          <PrivateRouter>
                            <MyPage />
                          </PrivateRouter>
                        }
                      />
                      <Route path='/oauth/redirect' element={<OAuthRedirect />} />
                    </Routes>
                  </SessionHandler>
                </AsyncBoundary>
              </PageLayout>
            </ResponsiveContainer>
          </BrowserRouter>
        </ToastProvider>
      </UserInfoProvider>
    </ThemeProvider>
  );
};

export default App;
