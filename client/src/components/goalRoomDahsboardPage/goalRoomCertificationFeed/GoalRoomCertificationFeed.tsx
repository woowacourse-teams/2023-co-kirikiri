import * as S from './GoalRoomCertificationFeed.styles';
import SVGIcon from '@components/icons/SVGIcon';
import { GoalRoomBrowseResponse } from '@myTypes/goalRoom/remote';
import { StyledImage } from './GoalRoomCertificationFeed.styles';

type GoalRoomCertificationFeedProps = {
  goalRoomData: GoalRoomBrowseResponse;
};

// TODO: 사진 누르면 모달로 사진 크게 보여주기

const GoalRoomCertificationFeed = ({ goalRoomData }: GoalRoomCertificationFeedProps) => {
  const { checkFeeds } = goalRoomData;

  return (
    <S.CertificationFeedWrapper>
      <div>
        <h2>인증 피드</h2>
        <button aria-labelledby='이미지 피드 전체보기'>
          <span>전체보기</span>
          <SVGIcon name='RightArrowIcon' aria-hidden='true' />
        </button>
      </div>
      <S.ImageGrid>
        {checkFeeds.map((feed) => {
          return (
            <button key={feed.id} aria-label='이미지 크게보기'>
              <StyledImage src={feed.imageUrl} alt={`Image ${feed.id}`} />
            </button>
          );
        })}
      </S.ImageGrid>
    </S.CertificationFeedWrapper>
  );
};

export default GoalRoomCertificationFeed;
