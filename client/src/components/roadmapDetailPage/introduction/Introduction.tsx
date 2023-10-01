import { RoadmapDetailType } from '@myTypes/roadmap/internal';
import * as S from './Introduction.styles';
import { useEffect, useRef, useState } from 'react';

type IntroductionProps = {
  roadmapInfo: RoadmapDetailType;
};

const Introduction = ({ roadmapInfo }: IntroductionProps) => {
  const [isExpanded, setIsExpanded] = useState(false);
  const [showMoreButton, setShowMoreButton] = useState(false);
  const introRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!introRef.current) return;

    const element = introRef.current;
    if (element.scrollHeight > element.clientHeight) {
      setShowMoreButton(true);
    }
  }, []);

  const toggleExpand = () => {
    setIsExpanded((prev) => !prev);
  };

  return (
    <S.IntroductionWrapper>
      <S.Introduction isExpanded={isExpanded} ref={introRef}>
        <p>
          <div>설명</div>
          {roadmapInfo.introduction}
        </p>
        <p>
          <div>본문</div>
          {roadmapInfo.content.content === ''
            ? '로드맵에 대한 설명이 없어요🥲'
            : roadmapInfo.content.content}
        </p>
      </S.Introduction>
      {showMoreButton && !isExpanded && (
        <>
          <S.LineShadow />
          <S.ReadMoreButton onClick={toggleExpand}>더 보기</S.ReadMoreButton>
        </>
      )}
    </S.IntroductionWrapper>
  );
};

export default Introduction;
