import { RaodmapValueType } from '@/myTypes/roadmap/roadmapCreate';
import { createContext, PropsWithChildren, useRef, useState } from 'react';
import Category, { DummyCategoryType } from '../category/category';
import Description from '../description/description';
import Difficulty, { DummyDifficultyType } from '../difficulty/difficulty';
import MainText from '../mainText/mainText';
import Period from '../period/period';
import Roadmap from '../roadmap/Roadmap';
import Title from '../title/title';

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
  const [, setRoadmapValue] = useState<RaodmapValueType>({
    categoryId: null,
    title: null,
    introduction: null,
    content: null,
    difficulty: null,
    requiredPeriod: null,
    roadmapNodes: [],
  });

  const getSelectedCategoryId = (category: keyof DummyCategoryType | null) => {
    setRoadmapValue((prev) => ({
      ...prev,
      categoryId: category,
    }));
  };

  const getSelectedDifficulty = (difficulty: keyof DummyDifficultyType | null) => {
    setRoadmapValue((prev) => ({
      ...prev,
      difficulty,
    }));
  };

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const target = e.target as HTMLFormElement;

    setRoadmapValue((prev) => ({
      ...prev,
      title: target.roadmapTitle.value,
      introduction: target.introduction.value,
      content: target.content.value ?? null,
      requiredPeriod: target.requiredPeriod.value,
      roadmapNodes: [],
    }));
  };

  return (
    <RefProvider>
      <form onSubmit={handleSubmit}>
        <Category getSelectedCategoryId={getSelectedCategoryId} />
        <Title />
        <Description />
        <Difficulty getSelectedDifficulty={getSelectedDifficulty} />
        <MainText />
        <Period />
        <Roadmap />
        <button>done!!</button>
      </form>
    </RefProvider>
  );
};

export default RoadmapCreateForm;
