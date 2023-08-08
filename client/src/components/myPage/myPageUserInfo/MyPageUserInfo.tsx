import * as S from './MyPageUserInfo.styles';
import { useUserInfoContext } from '@components/_providers/UserInfoProvider';
import { BASE_URL } from '@apis/axios/client';
import SVGIcon from '@components/icons/SVGIcon';

const MyPageUserInfo = () => {
  const { userInfo } = useUserInfoContext();

  return (
    <S.UserInfoWrapper>
      <S.UserDetails>
        <S.UserInfoImageContainer>
          <S.UserInfoImage
            src={BASE_URL + userInfo.profileImageUrl}
            alt='유저 프로필 이미지'
          />
        </S.UserInfoImageContainer>
        <S.UserNickname>
          <SVGIcon name={userInfo.gender === 'MALE' ? 'MaleFace' : 'FemaleFace'} />
          <h2>{userInfo.nickname}</h2>
        </S.UserNickname>
        <S.UserIdentifier>@ {userInfo.identifier}</S.UserIdentifier>
      </S.UserDetails>
    </S.UserInfoWrapper>
  );
};

export default MyPageUserInfo;
