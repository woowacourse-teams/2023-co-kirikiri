import SVGIcon from '@components/icons/SVGIcon';
import * as S from './NavBar.styles';
import { useUserInfoContext } from '@components/_providers/UserInfoProvider';
import isValidUserInfo from '@utils/user/isValidUserInfo';
import { BASE_URL } from '@apis/axios/client';
import { useLogout } from '@hooks/queries/user';

type NavBarProps = {
  isSwitchOn: boolean;
};

const NavBar = ({ isSwitchOn }: NavBarProps) => {
  const { userInfo } = useUserInfoContext();
  const { logout } = useLogout();

  return (
    <S.NavBar isNavBarOpen={isSwitchOn}>
      <S.Nav>
        <S.Links>
          <S.NavTitle to='/'>
            <S.ItemIcon>
              <SVGIcon name='CokiriIcon' />
            </S.ItemIcon>
          </S.NavTitle>
          <S.Item to='/roadmap-list'>
            <S.ItemIcon>
              <SVGIcon name='RoadmapIcon' />
            </S.ItemIcon>
            <S.Text>로드맵</S.Text>
          </S.Item>
          {isValidUserInfo(userInfo) && (
            <S.Item to='/mypage'>
              <S.ItemIcon>
                <SVGIcon name='GoalRoomIcon' />
              </S.ItemIcon>
              <S.Text>마이 페이지</S.Text>
            </S.Item>
          )}
        </S.Links>

        {isValidUserInfo(userInfo) ? (
          <S.Links>
            <S.Item to='/myPage'>
              <S.ItemIcon>
                <S.UserProfileImage src={BASE_URL + userInfo.profileImageUrl} alt='' />
              </S.ItemIcon>
              <S.Text>{userInfo.nickname}</S.Text>
            </S.Item>
            <S.Item to='/login' onClick={logout}>
              <S.ItemIcon>
                <SVGIcon name='LogoutIcon' />
              </S.ItemIcon>
              <S.Text>로그아웃</S.Text>
            </S.Item>
          </S.Links>
        ) : (
          <S.Links>
            <S.Item to='/login'>
              <S.ItemIcon>
                <SVGIcon name='LoginIcon' />
              </S.ItemIcon>
              <S.Text>로그인</S.Text>
            </S.Item>
          </S.Links>
        )}
      </S.Nav>
    </S.NavBar>
  );
};

export default NavBar;
