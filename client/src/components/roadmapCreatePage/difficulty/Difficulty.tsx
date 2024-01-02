/* eslint-disable react/no-unused-prop-types */
import { useSelect } from '@/hooks/_common/useSelect';
import { DifficultiesType, DifficultyKeyType } from '@/myTypes/roadmap/internal';
import {
  getInvariantObjectKeys,
  getInvariantObjectValues,
  invariantOf,
} from '@/utils/_common/invariantType';
import { useEffect } from 'react';
import { Select } from 'ck-util-components';
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
  const { selectOption, selectedOption } = useSelect<DifficultyKeyType>();

  useEffect(() => {
    if (selectedOption === null) return;

    getSelectedDifficulty(selectedOption);
  }, [selectedOption]);

  return (
    <Select externalSelectedOption={selectedOption} onSelectChange={selectOption}>
      <S.DifficultyLabel>
        난이도<p>*</p>
      </S.DifficultyLabel>
      <S.DifficultyDescription>
        컨텐츠의 달성 난이도를 선택해주세요
      </S.DifficultyDescription>
      <Select.Trigger asChild>
        <S.TriggerButton type='button'>
          <S.DifficultyValue>{selectedOption ?? '선택안함'}</S.DifficultyValue>
        </S.TriggerButton>
      </Select.Trigger>
      <Select.OptionGroup asChild>
        <S.Wrapper>
          {getInvariantObjectKeys(invariantOf(Difficulties)).map((difficulty, idx) => {
            return (
              <Select.Option
                id={getInvariantObjectValues(invariantOf(Difficulties))[idx]}
                asChild
              >
                <S.DifficultyOption>
                  <Select.Option
                    id={getInvariantObjectValues(invariantOf(Difficulties))[idx]}
                    asChild
                  >
                    <S.OptionIndicator />
                  </Select.Option>
                  {Difficulties[difficulty]}
                </S.DifficultyOption>
              </Select.Option>
            );
          })}
        </S.Wrapper>
      </Select.OptionGroup>
    </Select>
  );
};

export default Difficulty;
