import * as S from './inputDescription.styles';

type InputDescriptionProps = {
  text: string;
};

const InputDescription = (props: InputDescriptionProps) => {
  const { text } = props;

  return <S.InputDescription>{text}</S.InputDescription>;
};

export default InputDescription;
