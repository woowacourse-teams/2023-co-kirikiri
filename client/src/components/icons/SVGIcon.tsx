import svg from '@/myTypes/_common/svg';
import * as Icons from '@components/icons/svgIcons';

const SVGIcon = ({ name, size = '30', color = '#000', noFill = false }: svg) => {
  const SVGIconComponent = Icons[name];

  return (
    <SVGIconComponent
      data-testid='my-svg-icon'
      width={size}
      fill={color}
      style={{ color: noFill ? color : 'inherit' }}
    />
  );
};

export default SVGIcon;
