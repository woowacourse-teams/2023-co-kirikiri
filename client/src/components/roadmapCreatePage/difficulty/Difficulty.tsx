import { useSelect } from '@/hooks/_common/useSelect';
import { Select, SelectBox } from '../selector/selectBox';
import { S } from './Difficulty.styles';

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
      <Select.Label asChild>
        <S.DifficultyLabel>
          난이도<p>*</p>
        </S.DifficultyLabel>
      </Select.Label>
      <Select.Description asChild>
        <S.DifficultyDescription>
          컨텐츠의 달성 난이도를 선택해주세요
        </S.DifficultyDescription>
      </Select.Description>
      <Select.Trigger asChild>
        <S.TriggerButton />
      </Select.Trigger>
      <Select.OptionGroup asChild>
        <S.Wrapper>
          {Object.keys(DummyDifficulty).map((difficultyId) => {
            return (
              <Select.Option id={Number(difficultyId)} asChild>
                <S.DifficultyOption>
                  <Select.Indicator id={Number(difficultyId)} asChild>
                    <S.OptionIndicator />
                  </Select.Indicator>
                  {DummyDifficulty[Number(difficultyId)]}
                </S.DifficultyOption>
              </Select.Option>
            );
          })}
        </S.Wrapper>
      </Select.OptionGroup>
    </SelectBox>
  );
};

export default Difficulty;
