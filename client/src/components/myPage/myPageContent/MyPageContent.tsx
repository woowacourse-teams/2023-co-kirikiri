import MyPageUserInfo from '@components/myPage/myPageUserInfo/MyPageUserInfo';
import MyPageGoalRoomTab from '@components/myPage/myPageGoalRoomTab/MyPageGoalRoomTab';
// import MyPageRoadmapTab from '@components/myPage/myPageRoadmapTab/MyPageRoadmapTab';

const MyPageContent = () => {
  return (
    <>
      <MyPageUserInfo />
      <MyPageGoalRoomTab />
      {/* <MyPageRoadmapTab /> */}
    </>
  );
};

export default MyPageContent;
