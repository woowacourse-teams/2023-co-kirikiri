import { TITLE_MAX_LENGTH } from '@/constants/roadmap/regex';
import { useValidateInput } from '@hooks/_common/useValidateInput';
import InputField from '../input/inputField/InputField';
import InputLabel from '../input/inputLabel/InputLebel';
import TextCount from '../input/textCount/TextCount';
import * as S from './title.styles';

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
          name='roadmapTitle'
        />
        <TextCount maxCount={20} currentCount={value.length} />
      </S.FieldWrapper>
      <S.ErrorMessage>{errorMessage}</S.ErrorMessage>
    </S.Container>
  );
};

export default Title;
