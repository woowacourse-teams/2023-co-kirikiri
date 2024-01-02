import { Select } from 'ck-util-components';
import { goalRoomFilter } from '@/constants/goalRoom/goalRoomFilter';
import { useState } from 'react';
import * as S from './goalRoomList.styles';
import { getInvariantObjectKeys, invariantOf } from '@/utils/_common/invariantType';

interface GoalRoomFilterProps {
  sortedOption: (typeof goalRoomFilter)[keyof typeof goalRoomFilter];
  selectOption: (option: keyof typeof goalRoomFilter) => void;
}

const GoalRoomFilter = ({ sortedOption, selectOption }: GoalRoomFilterProps) => {
  const [filterOpen, setFilterOpen] = useState(false);

  const toggleFilter = () => {
    if (filterOpen) {
      setFilterOpen(false);
    } else {
      setFilterOpen(true);
    }
  };

  return (
    <S.FilterWrapper>
      <Select
        externalSelectedOption={sortedOption}
        onSelectChange={selectOption}
        externalOpen={filterOpen}
        onOpenChange={toggleFilter}
      >
        <Select.Trigger asChild>
          <S.FilterTrigger>{sortedOption}</S.FilterTrigger>
        </Select.Trigger>
        <Select.OptionGroup>
          <S.FilterOptionWrapper>
            {getInvariantObjectKeys(invariantOf(goalRoomFilter)).map((item) => {
              return (
                <Select.Option id={item} onOptionClick={() => toggleFilter()} asChild>
                  <S.FilterOption>{goalRoomFilter[item]}</S.FilterOption>
                </Select.Option>
              );
            })}
          </S.FilterOptionWrapper>
        </Select.OptionGroup>
      </Select>
    </S.FilterWrapper>
  );
};

export default GoalRoomFilter;
