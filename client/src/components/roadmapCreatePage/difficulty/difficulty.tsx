import { useSelect } from '@/hooks/_common/useSelect';
import {
  Description,
  Indicator,
  Label,
  Option,
  OptionGroup,
  SelectBox,
  Trigger,
} from '../selector/selectBox';
import { S } from './difficulty.style';

// 임시 더미데이터
type DummyDifficultyType = {
  [key: string]: string;
};

const DummyDifficulty: DummyDifficultyType = {
  1: '매우쉬움',
  2: '쉬움',
  3: '보통',
  4: '어려움',
  5: '매우어려움',
};

const Difficulty = () => {
  const { selectOption } = useSelect<number>();
  return (
    <SelectBox externalSelectState={selectOption}>
      <Label asChild>
        <S.DifficultyLabel>
          난이도<p>*</p>
        </S.DifficultyLabel>
      </Label>
      <Description asChild>
        <S.DifficultyDescription>
          컨텐츠의 달성 난이도를 선택해주세요
        </S.DifficultyDescription>
      </Description>
      <Trigger asChild>
        <S.TriggerButton />
      </Trigger>
      <OptionGroup asChild>
        <S.Wrapper>
          {Object.keys(DummyDifficulty).map((difficultyId) => {
            return (
              <Option id={Number(difficultyId)} asChild>
                <S.DifficultyOption>
                  <Indicator id={Number(difficultyId)} asChild>
                    <S.OptionIndicator />
                  </Indicator>
                  {DummyDifficulty[Number(difficultyId)]}
                </S.DifficultyOption>
              </Option>
            );
          })}
        </S.Wrapper>
      </OptionGroup>
    </SelectBox>
  );
};

export default Difficulty;
