import { lazy, PropsWithChildren, Suspense, useEffect } from 'react';
import { BrowserRouter, Route, Routes, useNavigate } from 'react-router-dom';
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
import UserInfoProvider, {
  useUserInfoContext,
} from './components/_providers/UserInfoProvider';
import RoadmapSearchResult from './components/roadmapListPage/roadmapSearch/RoadmapSearchResult';
import MainPage from '@pages/mainPage/MainPage';
import useToast from '@hooks/_common/useToast';
import OAuthRedirect from './components/loginPage/OAuthRedirect';
import AsyncBoundary from './components/_common/errorBoundary/AsyncBoundary';
import SessionHandler from '@components/_common/sessionHandler/SessionHandler';
import RouteChangeTracker from '@components/_common/routeChangeTracker/RouteChangeTracker';

const GoalRoomDashboardPage = lazy(
  () => import('@pages/goalRoomDashboardPage/GoalRoomDashboardPage')
);
const GoalRoomListPage = lazy(() => import('@pages/goalRoomListPage/GoalRoomListPage'));
const GoalRoomCreatePage = lazy(
  () => import('@pages/goalRoomCreatePage/GoalRoomCreatePage')
);

const PrivateRouter = (props: PropsWithChildren) => {
  const { children } = props;
  const { userInfo } = useUserInfoContext();
  const { triggerToast } = useToast();
  const navigate = useNavigate();

  useEffect(() => {
    if (userInfo.id === null) {
      navigate('/login');
      triggerToast({ message: '로그인이 필요한 서비스입니다.' });
    }
  }, [userInfo.id, navigate]);

  return <>{children}</>;
};

const App = () => {
  return (
    <ThemeProvider theme={theme}>
      <GlobalStyle />
      <UserInfoProvider>
        <ToastProvider>
          <BrowserRouter>
            <RouteChangeTracker>
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
            </RouteChangeTracker>
          </BrowserRouter>
        </ToastProvider>
      </UserInfoProvider>
    </ThemeProvider>
  );
};

export default App;
