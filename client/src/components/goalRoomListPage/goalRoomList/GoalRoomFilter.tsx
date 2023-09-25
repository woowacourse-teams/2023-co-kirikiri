import { Select } from '@/components/roadmapCreatePage/selector/SelectBox';
import { goalRoomFilter } from '@/constants/goalRoom/goalRoomFilter';
import React, { useState } from 'react';
import * as S from './goalRoomList.styles';

const GoalRoomFilter = ({
  children,
}: {
  children: (
    selectedOption: (typeof goalRoomFilter)[keyof typeof goalRoomFilter]
  ) => React.ReactNode;
}) => {
  const [selectedOption, setSelectedOption] = useState<
    (typeof goalRoomFilter)[keyof typeof goalRoomFilter]
  >(goalRoomFilter['1']);

  const selectFilterOption = (id: number) => {
    // eslint-disable-next-line no-prototype-builtins
    if (goalRoomFilter.hasOwnProperty(id)) {
      setSelectedOption(goalRoomFilter[id as keyof typeof goalRoomFilter]);
    }
  };

  return (
    <S.FilterWrapper>
      <Select externalSelectState={selectFilterOption}>
        <Select.Trigger asChild>
          <S.FilterTrigger>{selectedOption}</S.FilterTrigger>
        </Select.Trigger>
        {children(selectedOption)}
      </Select>
    </S.FilterWrapper>
  );
};

export default GoalRoomFilter;
