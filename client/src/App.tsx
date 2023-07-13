import { BrowserRouter, Route, Routes } from 'react-router-dom';

const App = () => {
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
