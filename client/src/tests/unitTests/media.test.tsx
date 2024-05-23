import 'jest-styled-components';
import styled from 'styled-components';
import media from '@styles/media';
import { render } from '@testing-library/react';

describe('미디어 쿼리 스타일 테스트', () => {
  it('모바일 스타일 적용 검증', () => {
    const StyledComponent = styled.div`
      ${media.mobile`
          background-color: red;
      `}
    `;
    const { container } = render(<StyledComponent />);
    expect(container.firstChild).toHaveStyleRule('background-color', 'red', {
      media: '(max-width:480px)',
    });
  });

  it('태블릿 스타일 적용 검증', () => {
    const StyledComponent = styled.div`
      ${media.tablet`
          background-color: blue;
      `}
    `;
    const { container } = render(<StyledComponent />);
    expect(container.firstChild).toHaveStyleRule('background-color', 'blue', {
      media: '(max-width:768px)',
    });
  });

  it('데스크탑 스타일 적용 검증', () => {
    const StyledComponent = styled.div`
      ${media.desktop`
          background-color: green;
      `}
    `;
    const { container } = render(<StyledComponent />);
    expect(container.firstChild).toHaveStyleRule('background-color', 'green', {
      media: '(min-width:1137px)',
    });
  });
});
