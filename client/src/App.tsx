import styled from 'styled-components';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Counter from './Counter';

const Container = styled.div`
  position: fixed;
  display: flex;
`;

const App = () => {
  return (
    <Container>
      <BrowserRouter>
        <Routes>
          <Route
            path='/'
            element={
              <div>
                <Counter />
                <input />
              </div>
            }
          />
        </Routes>
      </BrowserRouter>
    </Container>
  );
};

export default App;
