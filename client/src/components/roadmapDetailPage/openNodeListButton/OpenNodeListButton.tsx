import { NodeListIcon } from '@components/icons/svgIcons';
import Button from '@components/_common/button/Button';
import * as S from './OpenNodeListButton.styles';

const OpenNodeListButton = () => {
  return (
    <S.OpenNodeListButton>
      <NodeListIcon width={120} height={120} />
      <Button>로드맵 확인하기</Button>
    </S.OpenNodeListButton>
  );
};

export default OpenNodeListButton;
