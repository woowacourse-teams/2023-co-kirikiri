import { GoalRoomRecruitmentStatus } from '@myTypes/goalRoom/internal';

const recruitmentStatus: { [key in GoalRoomRecruitmentStatus]: string } = {
  RECRUITING: '모집중',
  RUNNING: '진행중',
  COMPLETED: '완료됨',
  RECRUIT_COMPLETED: '모집 완료',
};

export default recruitmentStatus;
