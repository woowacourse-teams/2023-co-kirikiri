import * as S from './inputLabel.styles';

type InputLabelProps = {
  text: string;
};

const InputLabel = (props: InputLabelProps) => {
  const { text } = props;

  return (
    <S.InputLabel>
      {text} <p>*</p>
    </S.InputLabel>
  );
};

export default InputLabel;
