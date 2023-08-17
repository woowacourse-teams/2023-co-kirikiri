import { ImageUploadIcon } from '@/components/icons/svgIcons';
import { useUploadImage } from '@/hooks/_common/useUploadImage';
import React, { ChangeEvent } from 'react';
import * as S from './roadmap.styles';

type RoadmapItemProps = {
  roadmapNumber: number;
  itemId: number;
  getRoadmapItemTitle: <T extends HTMLInputElement | HTMLTextAreaElement>(
    e: React.ChangeEvent<T>,
    itemId: number
  ) => void;
  getNodeImage: (nodeImage: File, itemId: number) => void;
};

const RoadmapItem = ({
  roadmapNumber,
  itemId,
  getRoadmapItemTitle,
  getNodeImage,
}: RoadmapItemProps) => {
  const { imagePreviews, getFileFromElement, showPrevImage } = useUploadImage();

  const handleImageChange = (event: ChangeEvent<HTMLInputElement>) => {
    Array.from(getFileFromElement(event)).forEach((uploadedFile) => {
      getNodeImage(uploadedFile, itemId);

      if (uploadedFile) {
        showPrevImage(uploadedFile);
      }
    });
  };

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
            placeholder='로드맵의 제목을 입렵해주세요'
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
          placeholder='로드맵의 본문을 입렵해주세요'
        />
        <S.ImageSection>
          {imagePreviews?.map((preview) => {
            return <S.UploadedNodeImage src={preview} alt='로드맵 노드 이미지' />;
          })}
          <S.NodeInputLabel htmlFor={`fileInput-${itemId}`}>
            <ImageUploadIcon height='30px' width='30px' />
            <S.NodeFileInput
              id={`fileInput-${itemId}`}
              type='file'
              onChange={handleImageChange}
            />
          </S.NodeInputLabel>
        </S.ImageSection>
      </S.BodyFieldWrapper>
    </>
  );
};

export default RoadmapItem;
