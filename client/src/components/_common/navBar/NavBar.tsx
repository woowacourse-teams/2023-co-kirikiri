import SVGIcon from '@components/icons/SVGIcon';
import * as S from './NavBar.styles';

type NavBarProps = {
  isSwitchOn: boolean;
};

const NavBar = ({ isSwitchOn }: NavBarProps) => {
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
          <S.Item to='/goalroom-list'>
            <S.ItemIcon>
              <SVGIcon name='GoalRoomIcon' />
            </S.ItemIcon>
            <S.Text>골룸</S.Text>
          </S.Item>
        </S.Links>
        <S.Links>
          <S.Item to='/login'>
            <S.ItemIcon>
              <SVGIcon name='LoginIcon' />
            </S.ItemIcon>
            <S.Text>로그인</S.Text>
          </S.Item>
        </S.Links>
      </S.Nav>
    </S.NavBar>
  );
};

export default NavBar;
