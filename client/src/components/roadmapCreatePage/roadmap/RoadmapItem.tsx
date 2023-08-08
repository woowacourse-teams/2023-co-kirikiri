import React, { ChangeEvent } from 'react';
import * as S from './roadmap.styles';

type RoadmapItemProps = {
  roadmapNumber: number;
  itemId: number;
  getRoadmapItemTitle: <T extends HTMLInputElement | HTMLTextAreaElement>(
    e: React.ChangeEvent<T>,
    itemId: number
  ) => void;
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
          <S.NodeTitleInputField
            onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
              getRoadmapItemTitle<HTMLInputElement>(e, itemId)
            }
            maxLength={40}
            name='title'
          />
        </S.TitleFieldWrapper>
      </S.TitleWrapper>
      <S.BodyFieldWrapper>
        <S.NodeBodyInputField
          onChange={(e: ChangeEvent<HTMLTextAreaElement>) =>
            getRoadmapItemTitle<HTMLTextAreaElement>(e, itemId)
          }
          maxLength={2000}
          name='content'
        />
      </S.BodyFieldWrapper>
    </>
  );
};

export default RoadmapItem;
