import * as S from './GoalRoomCertificationFeed.styles';
import SVGIcon from '@components/icons/SVGIcon';

const GoalRoomCertificationFeed = () => {
  return (
    <S.CertificationFeedWrapper>
      <div>
        <h2>인증 피드</h2>
        <button>
          <span>전체보기</span>
          <SVGIcon name='RightArrowIcon' />
        </button>
      </div>
      <div>인증</div>
    </S.CertificationFeedWrapper>
  );
};

export default GoalRoomCertificationFeed;
