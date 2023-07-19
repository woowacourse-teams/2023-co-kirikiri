import { useValidateInput } from '@/hooks/_common/useValidateInput';
import InputField from '../input/inputField/inputField';
import InputLabel from '../input/inputLabel/inputLebel';
import TextCount from '../input/textCount/textCount';
import * as S from './description.styles';

const DESCRIPTION_MAX_LENGTH = { rule: /^.{1,150}$/, message: '소개글을 입력해주세요' };

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
        />
        <TextCount maxCount={150} currentCount={value.length} />
      </S.FieldWrapper>
      <S.ErrorMessage>{errorMessage}</S.ErrorMessage>
    </S.Container>
  );
};

export default Description;
