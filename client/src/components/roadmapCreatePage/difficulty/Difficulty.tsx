/* eslint-disable react/no-unused-prop-types */
import { useSelect } from '@/hooks/_common/useSelect';
import { DifficultiesType, DifficultyKeyType } from '@/myTypes/roadmap/internal';
import { getInvariantObjectKeys, invariantOf } from '@/utils/_common/invariantType';
import { useEffect } from 'react';
import { Select, SelectBox } from '../selector/SelectBox';
import * as S from './difficulty.styles';

const Difficulties: DifficultiesType = {
  VERY_EASY: '매우쉬움',
  EASY: '쉬움',
  NORMAL: '보통',
  DIFFICULT: '어려움',
  VERY_DIFFICULT: '매우어려움',
};

type DifficultyProps = {
  getSelectedDifficulty: (difficulty: DifficultyKeyType | null) => void;
};

const Difficulty = ({ getSelectedDifficulty }: DifficultyProps) => {
  const { selectOption, selectedOption } = useSelect<number>();

  useEffect(() => {
    if (selectedOption === null) return;

    getSelectedDifficulty(
      getInvariantObjectKeys(invariantOf(Difficulties))[selectedOption]
    );
  }, [selectedOption]);

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
        <S.TriggerButton>
          <Select.Value asChild>
            {({ selectedId }: { selectedId: number | null }) => {
              return (
                <S.DifficultyValue>
                  {selectedId === null
                    ? '선택안함'
                    : Difficulties[
                        getInvariantObjectKeys(invariantOf(Difficulties))[selectedId]
                      ]}
                </S.DifficultyValue>
              );
            }}
          </Select.Value>
        </S.TriggerButton>
      </Select.Trigger>
      <Select.OptionGroup asChild>
        <S.Wrapper>
          {getInvariantObjectKeys(invariantOf(Difficulties)).map((difficulty, idx) => {
            return (
              <Select.Option id={idx} asChild>
                <S.DifficultyOption>
                  <Select.Indicator id={idx} asChild>
                    <S.OptionIndicator />
                  </Select.Indicator>
                  {Difficulties[difficulty]}
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
