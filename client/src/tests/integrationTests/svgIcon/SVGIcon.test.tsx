import { render } from '@testing-library/react';
import SVGIcon from '@components/icons/SVGIcon';

describe('SVGIcon 컴포넌트', () => {
  it('기본 속성으로 렌더링', () => {
    const { getByTestId } = render(<SVGIcon name='PersonIcon' />);
    const svgElement = getByTestId('my-svg-icon');
    expect(svgElement).toBeInTheDocument();
    expect(svgElement).toHaveAttribute('width', '30');
    expect(svgElement).toHaveAttribute('fill', '#000');
  });

  it('크기 변경', () => {
    const { getByTestId } = render(<SVGIcon name='PersonIcon' size='50' />);
    const svgElement = getByTestId('my-svg-icon');
    expect(svgElement).toHaveAttribute('width', '50');
  });

  it('색상 변경', () => {
    const { getByTestId } = render(<SVGIcon name='PersonIcon' color='red' />);
    const svgElement = getByTestId('my-svg-icon');
    expect(svgElement).toHaveAttribute('fill', 'red');
  });

  it('noFill 속성 적용', () => {
    const { getByTestId } = render(<SVGIcon name='PersonIcon' noFill />);
    const svgElement = getByTestId('my-svg-icon');
    expect(svgElement.style.color).toBe('rgb(0, 0, 0)');
  });
});
