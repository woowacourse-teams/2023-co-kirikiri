import { useValidateInput } from '@/hooks/_common/useValidateInput';
import InputField from '../input/inputField/inputField';
import InputLabel from '../input/inputLabel/inputLebel';
import TextCount from '../input/textCount/textCount';
import * as S from './title.styles';

const TITLE_MAX_LENGTH = { rule: /^.{0,20}$/, message: '' };

const Title = () => {
  const { handleInputChange, checkBlank, errorMessage, value } = useValidateInput([
    TITLE_MAX_LENGTH,
  ]);

  return (
    <S.Container>
      <InputLabel text='제목' />
      <S.FieldWrapper>
        <InputField
          placeholder='컨텐츠의 제목을 작성해주세요'
          handleInputChange={handleInputChange}
          maxLength={20}
          checkBlank={checkBlank}
        />
        <TextCount maxCount={150} currentCount={value.length} />
      </S.FieldWrapper>
      <S.ErrorMessage>{errorMessage}</S.ErrorMessage>
    </S.Container>
  );
};

export default Title;
