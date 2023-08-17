import * as S from './GoalRoomDashboardChat.styles';
import SVGIcon from '@components/icons/SVGIcon';

const GoalRoomDashboardChat = () => {
  return (
    <S.ChatWrapper>
      <div>
        <h2>채팅</h2>
        <button>
          <span>전체보기</span>
          <SVGIcon name='RightArrowIcon' />
        </button>
      </div>
      <div>채팅</div>
    </S.ChatWrapper>
  );
};

export default GoalRoomDashboardChat;
