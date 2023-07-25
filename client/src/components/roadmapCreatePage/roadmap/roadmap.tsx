import { useState } from 'react';
import RoadmapItem from './roadmapItem';
import * as S from './roadmap.styles';
import InputLabel from '../input/inputLabel/inputLebel';

const Roadmap = () => {
  const [roadmapNumber, setRoadmapNumber] = useState(1);
  const [roadmapArr, setRoadmapArr] = useState<number[]>([]);

  const addRoadmapTemplate = () => {
    setRoadmapNumber((prev) => prev + 1);
    setRoadmapArr([...roadmapArr, roadmapNumber]);
  };

  return (
    <>
      <InputLabel text='로드맵' />

      {roadmapArr.map((roadNum) => {
        return <RoadmapItem roadmapNumber={roadNum} />;
      })}
      <RoadmapItem roadmapNumber={roadmapNumber} />
      <S.AddButton onClick={addRoadmapTemplate}>로드맵 추가히기</S.AddButton>
    </>
  );
};

export default Roadmap;
