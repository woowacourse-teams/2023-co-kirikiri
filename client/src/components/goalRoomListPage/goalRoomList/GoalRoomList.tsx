import * as S from './goalRoomList.styles';
import GoalRoomItem from './GoalRoomItem';
import { useGoalRoomList } from '@/hooks/queries/goalRoom';
import useValidParams from '@/hooks/_common/useValidParams';
import { useState } from 'react';
import { FILTER_COND, goalRoomFilter } from '@/constants/goalRoom/goalRoomFilter';
import { useInfiniteScroll } from '@hooks/_common/useInfiniteScroll';
import WavyLoading from '@components/_common/wavyLoading/WavyLoading';
import { Link } from 'react-router-dom';
import GoalRoomFilter from './GoalRoomFilter';

const GoalRoomList = () => {
  const { id } = useValidParams<{ id: string }>();
  const [sortedOption, setSortedOption] = useState<
    (typeof goalRoomFilter)[keyof typeof goalRoomFilter]
  >(goalRoomFilter['1']);

  const {
    goalRoomListResponse: { responses: goalRoomList, hasNext },
    fetchNextPage,
  } = useGoalRoomList({
    roadmapId: Number(id),
    filterCond:
      sortedOption === goalRoomFilter['1'] ? FILTER_COND.latest : FILTER_COND.deadline,
  });

  const loadMoreRef = useInfiniteScroll({
    hasNextPage: hasNext,
    fetchNextPage,
  });

  const RecruitingGoalRoomList = goalRoomList.filter(
    (goalRoomInfo) => goalRoomInfo.status === 'RECRUITING'
  );

  const selectOption = (option: keyof typeof goalRoomFilter) => {
    setSortedOption(goalRoomFilter[option]);
  };

  return (
    <S.ListContainer role='main' aria-label='골룸 리스트'>
      <S.FilterBar>
        <p aria-live='polite'>
          모집중인 모임{' '}
          {
            goalRoomList.filter((goalRoomInfo) => goalRoomInfo.status === 'RECRUITING')
              .length
          }
          개
        </p>
        <GoalRoomFilter sortedOption={sortedOption} selectOption={selectOption} />
      </S.FilterBar>
      {RecruitingGoalRoomList.length ? (
        <S.ListWrapper aria-label='골룸 리스트'>
          {RecruitingGoalRoomList.map((goalRoomInfo) => (
            <GoalRoomItem key={goalRoomInfo.goalRoomId} {...goalRoomInfo} />
          ))}
        </S.ListWrapper>
      ) : (
        <S.NoContent>
          <div>현재 모집중인 모임이 존재하지 않아요</div>
          <div>모임을 생성해서 목표 달성을 함께 할 동료들을 모집 해 보세요!</div>
        </S.NoContent>
      )}
      {hasNext && <WavyLoading loadMoreRef={loadMoreRef} />}
      <Link to={`/roadmap/${Number(id)}/goalroom-create`}>
        <S.CreateGoalRoomButton>
          <div>모임 생성하러 가기</div>
        </S.CreateGoalRoomButton>
      </Link>
    </S.ListContainer>
  );
};

export default GoalRoomList;
