import { useState } from 'react';
import SVGIcon from '@components/icons/SVGIcon';
import { GoalRoomTodo } from '@myTypes/goalRoom/internal';
import * as S from './SingleTodo.styles';

/*
    TODO:
     1. 투두리스트 생성 모달 구현
     2. 투두리스트 실제 눌렀을 때 체크되는 기능 구현
*/

const SingleTodo = ({ todoContent }: { todoContent: GoalRoomTodo }) => {
  const { content, startDate, endDate } = todoContent;
  const [isTodoButtonHovered, setIsTodoButtonHovered] = useState(false);

  const handleMouseEnter = () => setIsTodoButtonHovered(true);
  const handleMouseLeave = () => setIsTodoButtonHovered(false);

  return (
    <S.Todo>
      <S.TodoButton
        onMouseEnter={handleMouseEnter}
        onMouseLeave={handleMouseLeave}
        aria-label='hidden'
      >
        <SVGIcon name={isTodoButtonHovered ? 'CheckedCircle' : 'EmptyCircle'} />
      </S.TodoButton>
      <S.TodoContent>{content}</S.TodoContent>
      <S.TodoDate>
        <S.TodoDateSpan>{startDate} ~</S.TodoDateSpan>
        <S.TodoDateSpan>{endDate}</S.TodoDateSpan>
      </S.TodoDate>
    </S.Todo>
  );
};

export default SingleTodo;
