import { useCollectRoadmapData } from '@/hooks/roadmap/useCollectRoadmapData';
import React, { createContext, PropsWithChildren, useRef } from 'react';
import Category from '../category/Category';
import Description from '../description/Description';
import Difficulty from '../difficulty/Difficulty';
import MainText from '../mainText/MainText';
import Period from '../period/Period';
import Roadmap from '../roadmap/Roadmap';
import RoadmapItem from '../roadmap/RoadmapItem';
import Tag from '../tag/Tag';
import Title from '../title/Title';
import * as S from './roadmapCreateForm.styles';

// ref공유를 위한 context - 다음 브랜치에서 파일 옮길 예정
const FormRefContext = createContext<{ ref: React.MutableRefObject<undefined> | null }>({
  ref: null,
});

const RefProvider = ({ children }: PropsWithChildren) => {
  const ref = useRef();

  return <FormRefContext.Provider value={{ ref }}>{children}</FormRefContext.Provider>;
};
//

const RoadmapCreateForm = () => {
  const {
    roadmapValue,
    getSelectedCategoryId,
    getSelectedDifficulty,
    getRoadmapItemTitle,
    getTags,
    handleSubmit,
    addNode,
  } = useCollectRoadmapData();

  return (
    <RefProvider>
      <form onSubmit={handleSubmit}>
        <Category getSelectedCategoryId={getSelectedCategoryId} />
        <Title />
        <Description />
        <Difficulty getSelectedDifficulty={getSelectedDifficulty} />
        <Tag getTags={getTags} />
        <MainText />
        <Period />
        <Roadmap>
          <>
            {roadmapValue.roadmapNodes.map((_, index) => {
              return (
                <RoadmapItem
                  roadmapNumber={index + 1}
                  itemId={index}
                  getRoadmapItemTitle={getRoadmapItemTitle}
                />
              );
            })}
            <S.AddButton onClick={addNode}>로드맵 추가하기</S.AddButton>
          </>
        </Roadmap>
        <button>done!!</button>
      </form>
    </RefProvider>
  );
};

export default RoadmapCreateForm;
