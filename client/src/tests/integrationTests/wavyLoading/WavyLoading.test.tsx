import { createRef, ReactElement } from 'react';
import {
  render as rtlRender,
  RenderOptions,
  RenderResult,
  screen,
} from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import WavyLoading from '@components/_common/wavyLoading/WavyLoading';
import { ThemeProvider } from 'styled-components';
import theme from '@styles/theme';

const render = (ui: ReactElement, options?: RenderOptions): RenderResult =>
  rtlRender(ui, {
    wrapper: (props) => <ThemeProvider theme={theme} {...props} />,
    ...options,
  });

describe('WavyLoading 컴포넌트', () => {
  it('정상적으로 렌더링 되어야 한다', () => {
    const loadMoreRef = createRef<null>();
    render(<WavyLoading loadMoreRef={loadMoreRef} />);
    const wavyLoadingElement = screen.getByTestId('wavy-loading');
    expect(wavyLoadingElement).toBeInTheDocument();
  });

  it('레퍼런스가 정상적으로 부착되어야 한다', () => {
    const loadMoreRef = createRef<HTMLDivElement>();
    render(<WavyLoading loadMoreRef={loadMoreRef} />);
    expect(loadMoreRef.current).not.toBeNull();
  });
});
