import * as S from './goalRoomList.styles';
import GoalRoomItem from './GoalRoomItem';
import { useGoalRoomList } from '@/hooks/queries/goalRoom';
import useValidParams from '@/hooks/_common/useValidParams';
import { useNavigate } from 'react-router-dom';
import GoalRoomFilter from './GoalRoomFilter';
import { Select } from '@/components/roadmapCreatePage/selector/SelectBox';
import { useState } from 'react';
import { goalRoomFilter } from '@/constants/goalRoom/goalRoomFilter';
import { useInfiniteScroll } from '@hooks/_common/useInfiniteScroll';
import WavyLoading from '@components/_common/wavyLoading/WavyLoading';

const GoalRoomList = () => {
  const { id } = useValidParams<{ id: string }>();
  const [sortedOption, setSortedOption] =
    useState<(typeof goalRoomFilter)[keyof typeof goalRoomFilter]>('마감 임박 순');
  const {
    goalRoomListResponse: { responses: goalRoomList, hasNext },
    fetchNextPage,
  } = useGoalRoomList({
    roadmapId: Number(id),
    filterCond: sortedOption === '참여 인원 순' ? 'PARTICIPATION_RATE' : 'LATEST',
  });

  const loadMoreRef = useInfiniteScroll({
    hasNextPage: hasNext,
    fetchNextPage,
  });

  const navigate = useNavigate();

  const moveCreateGoalRoomPage = () => {
    navigate(`/roadmap/${Number(id)}/goalroom-create`);
  };

  return (
    <S.ListContainer role='main' aria-label='골룸 리스트'>
      <S.FilterBar>
        <p aria-live='polite'>모집중인 골룸 {goalRoomList.length}개</p>
        <GoalRoomFilter>
          {(selectedOption) => {
            setSortedOption(selectedOption);
            return (
              <Select.OptionGroup asChild>
                <S.FilterOptionWrapper>
                  <Select.Option id={1} asChild>
                    <S.FilterOption>마감 임박순</S.FilterOption>
                  </Select.Option>
                  <Select.Option id={2} asChild>
                    <S.FilterOption>참여인원 순</S.FilterOption>
                  </Select.Option>
                </S.FilterOptionWrapper>
              </Select.OptionGroup>
            );
          }}
        </GoalRoomFilter>
      </S.FilterBar>
      <S.ListWrapper role='list' aria-label='골룸 리스트'>
        {goalRoomList.map((goalRoomInfo) => {
          return <GoalRoomItem key={goalRoomInfo.goalRoomId} {...goalRoomInfo} />;
        })}
        {hasNext && <WavyLoading loadMoreRef={loadMoreRef} />}
        <S.CreateGoalRoomButton onClick={moveCreateGoalRoomPage}>
          골룸 생성하러 가기
        </S.CreateGoalRoomButton>
      </S.ListWrapper>
    </S.ListContainer>
  );
};

export default GoalRoomList;
