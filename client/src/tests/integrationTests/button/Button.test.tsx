import {
  screen,
  fireEvent,
  RenderOptions,
  RenderResult,
  render as rtlRender,
} from '@testing-library/react';
import Button from '@components/_common/button/Button';
import { ReactElement } from 'react';
import { ThemeProvider } from 'styled-components';
import theme from '@styles/theme';

const render = (ui: ReactElement, options?: RenderOptions): RenderResult =>
  rtlRender(ui, {
    wrapper: (props) => <ThemeProvider theme={theme} {...props} />,
    ...options,
  });

describe('버튼 컴포넌트 테스트', () => {
  it('기본 렌더링 검증', () => {
    render(<Button>Click Me</Button>);
    expect(screen.getByText('Click Me')).toBeInTheDocument();
  });

  it('버튼 클릭 이벤트 테스트', () => {
    const mockClick = jest.fn();
    render(<Button onClick={mockClick}>Click Me</Button>);

    fireEvent.click(screen.getByText('Click Me'));
    expect(mockClick).toHaveBeenCalled();
  });

  it('버튼 스타일 변화 확인', () => {
    const { getByTestId } = render(<Button>Click Me</Button>);
    const button = getByTestId('button');

    expect(button).toHaveStyle('background-color: ButtonFace');
  });

  it('버튼 variant 속성 확인', () => {
    const { rerender } = render(<Button variant='primary'>Click Me</Button>);

    const buttonWithPrimary = screen.getByTestId('button');
    expect(buttonWithPrimary).toBeInTheDocument();

    rerender(<Button>Click Me</Button>);
    const buttonWithoutVariant = screen.getByTestId('button');
    expect(buttonWithoutVariant).toBeInTheDocument();
  });
});
