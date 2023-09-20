import LazyImage from '@/components/_common/lazyImage/lazyImage';
import { NodeType } from '@/myTypes/roadmap/internal';
import * as S from './roadmapNodeList.styles';

type RoadmapNodeListProps = {
  roadmapTitle: string;
  nodeInfo: NodeType[];
};

const RoadmapNodeList = ({ roadmapTitle, nodeInfo }: RoadmapNodeListProps) => {
  return (
    <S.RoadmapNodeList>
      <S.Title>
        🐘 <strong>{roadmapTitle}</strong>의 로드맵 🐘
      </S.Title>
      {nodeInfo.map((node, index) => {
        return (
          <S.NodeItemContainer>
            <S.NodeIndicator>{index + 1}</S.NodeIndicator>
            <S.NodeContent>
              <S.NodeTitle>{node.title}</S.NodeTitle>
              <S.NodeDescription>{node.description}</S.NodeDescription>
              {node.imageUrls.map((nodeImage) => {
                return (
                  <S.ImageWrapper>
                    <LazyImage
                      src={nodeImage}
                      style={{ width: '100%', height: '30rem' }}
                    />
                  </S.ImageWrapper>
                );
              })}
            </S.NodeContent>
          </S.NodeItemContainer>
        );
      })}
    </S.RoadmapNodeList>
  );
};

export default RoadmapNodeList;
