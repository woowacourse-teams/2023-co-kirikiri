import * as S from './textCount.styles';

type TextCountProps = {
  maxCount: number;
  currentCount: number;
};

const TextCount = (props: TextCountProps) => {
  const { maxCount, currentCount } = props;

  return (
    <S.Wrapper>
      {currentCount} / {maxCount}
    </S.Wrapper>
  );
};

export default TextCount;
