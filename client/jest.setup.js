import '@testing-library/jest-dom/extend-expect';
import '@testing-library/jest-dom';

global.IntersectionObserver.mockImplementation((callback) => {
  callback([{ isIntersecting: true }]);
  return {
    observe: jest.fn(),
    unobserve: jest.fn(),
    disconnect: jest.fn(),
  };
});
