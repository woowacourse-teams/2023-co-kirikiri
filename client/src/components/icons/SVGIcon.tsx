import svg from '@/types/_common/svg';
import * as Icons from '@components/icons/svgIcons';

const SVGIcon = ({ name, size = '30', color = '#000', noFill = false }: svg) => {
  const SVGIconComponent = Icons[name];

  return (
    <SVGIconComponent
      width={size}
      fill={color}
      style={{ color: noFill ? color : 'inherit' }}
    />
  );
};

export default SVGIcon;
