import { BrowserRouter, Route, Routes } from 'react-router-dom';
import GlobalStyle from '@/styles/GlobalStyle';

const App = () => {
  return (
    <>
      <GlobalStyle />
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
    </>
  );
};

export default App;
