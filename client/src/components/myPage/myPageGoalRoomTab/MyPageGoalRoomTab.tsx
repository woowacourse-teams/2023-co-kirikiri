import { MouseEvent, useState } from 'react';
import * as S from './MyPageGoalRoomTab.styles';
import { useMyPageGoalRoomList } from '@hooks/queries/goalRoom';
import { GoalRoomRecruitmentStatus } from '@myTypes/goalRoom/internal';
import recruitmentStatus from '@constants/goalRoom/recruitmentStatus';

const MyPageGoalRoomTab = () => {
  const [activeTab, setActiveTab] = useState<GoalRoomRecruitmentStatus>('RECRUITING');

  const { myGoalRoomList } = useMyPageGoalRoomList(activeTab);

  const handleTabClick = (event: MouseEvent<HTMLButtonElement>) => {
    const { tab } = event.currentTarget.dataset;

    if (tab) {
      setActiveTab(tab as GoalRoomRecruitmentStatus);
    }
  };

  return (
    <S.TabView>
      <S.TabList>
        <S.Tab
          data-tab='RECRUITING'
          isActive={activeTab === 'RECRUITING'}
          onClick={handleTabClick}
        >
          모집중인 골룸
        </S.Tab>
        <S.Tab
          data-tab='RUNNING'
          isActive={activeTab === 'RUNNING'}
          onClick={handleTabClick}
        >
          진행중인 골룸
        </S.Tab>
        <S.Tab
          data-tab='COMPLETED'
          isActive={activeTab === 'COMPLETED'}
          onClick={handleTabClick}
        >
          완료된 골룸
        </S.Tab>
      </S.TabList>

      <S.TabContent>
        <S.RoomHeader>
          <div>이름</div>
          <div>모집 상태</div>
          <div>참여 인원 / 모집 인원</div>
          <div>골룸 시작일</div>
          <div>골룸 종료일</div>
        </S.RoomHeader>

        {myGoalRoomList?.map((room, index) => (
          <S.RoomDetails key={room.goalRoomId} isOddRow={index % 2 === 1}>
            <div>
              <S.GoaRoomLink to={`/goalroom-dashboard/${room.goalRoomId}`}>
                {room.name}
              </S.GoaRoomLink>
            </div>
            <div>
              <S.RoomStatus>{recruitmentStatus[room.goalRoomStatus]}</S.RoomStatus>
            </div>
            <div>
              {room.currentMemberCount} / {room.limitedMemberCount}
            </div>
            <div>{room.startDate}</div>
            <div>{room.endDate}</div>
          </S.RoomDetails>
        ))}
      </S.TabContent>
    </S.TabView>
  );
};

export default MyPageGoalRoomTab;
