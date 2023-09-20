import { MouseEvent, useState } from 'react';
import * as S from './MyPageGoalRoomTab.styles';
import { useMyPageGoalRoomList } from '@hooks/queries/goalRoom';
import { GoalRoomRecruitmentStatus } from '@myTypes/goalRoom/internal';
import recruitmentStatus from '@constants/goalRoom/recruitmentStatus';
import SVGIcon from '@components/icons/SVGIcon';

const MyPageGoalRoomTab = () => {
  const [activeTab, setActiveTab] = useState<GoalRoomRecruitmentStatus>('RUNNING');

  const { myGoalRoomList } = useMyPageGoalRoomList(activeTab);

  const handleTabClick = (event: MouseEvent<HTMLButtonElement>) => {
    const { tab } = event.currentTarget.dataset;

    if (tab) {
      setActiveTab(tab as GoalRoomRecruitmentStatus);
    }
  };

  return (
    <>
      <S.ContentHeader>
        <span>나의 모임</span>
        <SVGIcon name='GoalRoomIcon' />
      </S.ContentHeader>
      <S.TabView>
        <S.TabList>
          <S.Tab
            data-tab='RECRUITING'
            isActive={activeTab === 'RECRUITING'}
            onClick={handleTabClick}
          >
            모집중인 모임
          </S.Tab>
          <S.Tab
            data-tab='RUNNING'
            isActive={activeTab === 'RUNNING'}
            onClick={handleTabClick}
          >
            진행중인 모임
          </S.Tab>
          <S.Tab
            data-tab='COMPLETED'
            isActive={activeTab === 'COMPLETED'}
            onClick={handleTabClick}
          >
            완료된 모임
          </S.Tab>
        </S.TabList>

        <S.TabContent>
          <S.RoomHeader>
            <div>이름</div>
            <div>모집 상태</div>
            <div>참여 인원 / 모집 인원</div>
            <div>모임 시작일</div>
            <div>모임 종료일</div>
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
    </>
  );
};

export default MyPageGoalRoomTab;
