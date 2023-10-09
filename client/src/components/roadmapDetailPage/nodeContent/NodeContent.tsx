import type { NodeType } from '@myTypes/roadmap/internal';
import * as S from './NodeContent.styles';
import SVGIcon from '@components/icons/SVGIcon';

type NodeContentProps = {
  node: NodeType;
  index: number;
};

const NodeContent = ({ node, index }: NodeContentProps) => {
  return (
    <S.SliderContent key={node.id}>
      <S.LeftContent>
        {node.imageUrls[0] ? (
          <S.NodeImg src={node.imageUrls[0]} />
        ) : (
          <S.NoImg>
            <SVGIcon name='NoImageIcon' size={50} />
            <div>No Image</div>
          </S.NoImg>
        )}
      </S.LeftContent>
      <S.Separator>
        <div />
        <div />
        <div />
      </S.Separator>
      <S.RightContent>
        <S.ContentTitle>
          <S.Step>{index + 1}.</S.Step>
          <p>{node.title}</p>
        </S.ContentTitle>
        <p>{node.description}</p>
      </S.RightContent>
    </S.SliderContent>
  );
};

export default NodeContent;
