import * as Icons from '@components/icons/svgIcons';

type SVGIconsProps = {
  name: keyof typeof Icons;
  size?: string | number;
  color?: string;
  noFill?: boolean;
};

export default SVGIconsProps;
