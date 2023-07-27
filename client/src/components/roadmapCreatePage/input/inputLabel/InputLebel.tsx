import * as S from './inputLabel.styles';

type InputLabelProps = {
  text: string;
};

const InputLabel = (props: InputLabelProps) => {
  const { text } = props;

  return (
    <S.InputLabel>
      {text} <abbr title='required'>*</abbr>
    </S.InputLabel>
  );
};

export default InputLabel;
