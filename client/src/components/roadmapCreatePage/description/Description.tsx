import { DESCRIPTION_MAX_LENGTH } from '@/constants/roadmap/regex';
import { useValidateInput } from '@/hooks/_common/useValidateInput';
import InputField from '../input/inputField/InputField';
import InputLabel from '../input/inputLabel/InputLebel';
import TextCount from '../input/textCount/TextCount';
import * as S from './description.styles';

const Description = () => {
  const { handleInputChange, validateInput, errorMessage, resetErrorMessage, value } =
    useValidateInput(DESCRIPTION_MAX_LENGTH);

  return (
    <S.Container>
      <InputLabel text='소개글' />
      <S.FieldWrapper>
        <InputField
          placeholder='컨텐츠를 소개하는 문장을 작성해주세요'
          handleInputChange={handleInputChange}
          maxLength={150}
          validateInput={validateInput}
          resetErrorMessage={resetErrorMessage}
          name='introduction'
          data-valid={validateInput}
        />
        <TextCount maxCount={150} currentCount={value.length} />
      </S.FieldWrapper>
      <S.ErrorMessage>{errorMessage}</S.ErrorMessage>
    </S.Container>
  );
};

export default Description;
