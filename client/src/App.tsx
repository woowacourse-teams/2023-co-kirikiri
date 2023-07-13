import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { useFetchUser } from '@hooks/queries/user';
import { useEffect } from 'react';

const App = () => {
  const { user } = useFetchUser();

  useEffect(() => {
    console.log(user, 'userInfo');
  }, [user]);

  return (
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
  );
};

export default App;
