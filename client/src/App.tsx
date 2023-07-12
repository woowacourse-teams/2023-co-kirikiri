import { BrowserRouter, Routes, Route } from 'react-router-dom';

const App = () => {
  return (
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
  );
};

export default App;
