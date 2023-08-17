import {
  DialogBackdrop,
  DialogBox,
  DialogContent,
  DialogTrigger,
} from '../_common/dialog/dialog';
import styled from 'styled-components';

const BackDrop = styled.div`
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;

  background-color: black;
`;

const Trigger = styled.div`
  width: 1rem;
  height: 1rem;
  background-color: black;
`;

const MainPage = () => {
  return (
    // 모달을 사용하고싶은 곳에서 최상위로 꼭 DialogBox를 감싸줘야합니다
    <DialogBox>
      {/* 눌렀을 때 모달이 열고 닫히는 trigger버튼이에요. 골룸에서는 전체보기 or 크게보기 버틴이 되겠죠? asChild 속성을 준 후에, 스타일링하고싶은 컴포넌트 꼭 1개만 자식으로 줘야해요 */}
      <DialogTrigger asChild>
        {/* 이렇게 커스텀 된 trigger버튼 1개만!! */}
        <Trigger />
      </DialogTrigger>
      {/* 모달 뜨면 뒤에 생기는 배경입니다! 이것도 asChild 속성을 준 후에 스타일링하고싶은 컴포넌트 꼭 1개만 자식으로 줘야합니다. */}
      <DialogBackdrop asChild>
        {/* 이렇게 커스텀 된 backdreop 1개만!! */}
        <BackDrop />
      </DialogBackdrop>
      {/* 모달의 내용물이 들어가는 부분입니다. 이부분은 asChild 속성을 주면 안되고, children을 줘서 마음대로 모달 내용물을 넣어주면 됩니다~ */}
      <DialogContent>
        <div>모달 내용물~</div>
      </DialogContent>
    </DialogBox>
  );
};

export default MainPage;
