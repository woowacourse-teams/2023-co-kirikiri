import * as S from './GoalRoomCertificationFeed.styles';
import SVGIcon from '@components/icons/SVGIcon';
import { GoalRoomBrowseResponse } from '@myTypes/goalRoom/remote';
import { StyledImage } from './GoalRoomCertificationFeed.styles';
import {
  DialogBackdrop,
  DialogBox,
  DialogContent,
  DialogTrigger,
} from '@components/_common/dialog/dialog';
import CertificationFeedModal from '@components/goalRoomDahsboardPage/goalRoomCertificationFeed/certificationFeedModal/CertificationFeedModal';
import { BASE_URL } from '@apis/axios/client';

type GoalRoomCertificationFeedProps = {
  goalRoomData: GoalRoomBrowseResponse;
};
// TODO: 사진 누르면 모달로 사진 크게 보여주기

const GoalRoomCertificationFeed = ({ goalRoomData }: GoalRoomCertificationFeedProps) => {
  const { checkFeeds } = goalRoomData;

  return (
    <DialogBox>
      <S.CertificationFeedWrapper>
        <div>
          <h2>인증 피드</h2>

          <DialogTrigger asChild>
            <button aria-labelledby='이미지 피드 전체보기'>
              <span>전체보기</span>
              <SVGIcon name='RightArrowIcon' aria-hidden='true' />
            </button>
          </DialogTrigger>
        </div>
        <S.ImageGrid>
          {checkFeeds.map((feed) => {
            return (
              <button key={feed.id} aria-label='이미지 크게보기'>
                <StyledImage src={BASE_URL + feed.imageUrl} alt={`Image ${feed.id}`} />
              </button>
            );
          })}
        </S.ImageGrid>
      </S.CertificationFeedWrapper>

      <DialogBackdrop asChild>
        <S.ModalBackdrop />
      </DialogBackdrop>

      <DialogContent>
        <CertificationFeedModal />
      </DialogContent>
    </DialogBox>
  );
};

export default GoalRoomCertificationFeed;
