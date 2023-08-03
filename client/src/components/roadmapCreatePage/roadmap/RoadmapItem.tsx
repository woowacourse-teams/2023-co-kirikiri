import * as S from './roadmap.styles';

type RoadmapItemProps = {
  roadmapNumber: number;
  itemId: number;
  getRoadmapItemTitle: (e: React.ChangeEvent<HTMLInputElement>, itemId: number) => void;
};

const RoadmapItem = ({
  roadmapNumber,
  itemId,
  getRoadmapItemTitle,
}: RoadmapItemProps) => {
  return (
    <>
      <S.TitleWrapper>
        <S.RoadmapNumber>{roadmapNumber}</S.RoadmapNumber>
        <S.TitleFieldWrapper>
          <input
            onChange={(e) => getRoadmapItemTitle(e, itemId)}
            maxLength={40}
            name='title'
          />
        </S.TitleFieldWrapper>
      </S.TitleWrapper>
      <S.BodyFieldWrapper>
        <input
          onChange={(e) => getRoadmapItemTitle(e, itemId)}
          maxLength={2000}
          name='content'
        />
      </S.BodyFieldWrapper>
    </>
  );
};

export default RoadmapItem;
