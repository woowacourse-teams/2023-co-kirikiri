import { useValidateInput } from '@hooks/_common/useValidateInput';
import InputField from '../input/inputField/inputField';
import InputLabel from '../input/inputLabel/inputLebel';
import TextCount from '../input/textCount/textCount';
import * as S from './title.styles';

const TITLE_MAX_LENGTH = { rule: /^.{1,20}$/, message: '제목은 필수로 입력해주세요' };

const Title = () => {
  const { handleInputChange, validateInput, resetErrorMessage, errorMessage, value } =
    useValidateInput(TITLE_MAX_LENGTH);

  return (
    <S.Container>
      <InputLabel text='제목' />
      <S.FieldWrapper>
        <InputField
          placeholder='컨텐츠의 제목을 작성해주세요'
          handleInputChange={handleInputChange}
          maxLength={20}
          validateInput={validateInput}
          resetErrorMessage={resetErrorMessage}
        />
        <TextCount maxCount={20} currentCount={value.length} />
      </S.FieldWrapper>
      <S.ErrorMessage>{errorMessage}</S.ErrorMessage>
    </S.Container>
  );
};

export default Title;
