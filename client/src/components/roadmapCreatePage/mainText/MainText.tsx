import { MAIN_TEXT_MAX_LENGTH } from '@/constants/roadmap/regex';
import { useValidateInput } from '@/hooks/_common/useValidateInput';
import InputField from '../input/inputField/InputField';
import InputLabel from '../input/inputLabel/InputLebel';
import TextCount from '../input/textCount/TextCount';
import * as S from './mainText.styles';

const MainText = () => {
  const { handleInputChange, validateInput, resetErrorMessage, value } =
    useValidateInput(MAIN_TEXT_MAX_LENGTH);

  return (
    <S.Container>
      <InputLabel text='본문' />
      <S.FieldWrapper>
        <InputField
          placeholder='컨텐츠를 자세하게 설명해주세요'
          handleInputChange={handleInputChange}
          maxLength={2000}
          validateInput={validateInput}
          resetErrorMessage={resetErrorMessage}
          name='content'
          data-valid={validateInput}
          value={value}
        />
        <S.TextCountWrapper>
          <TextCount maxCount={2000} currentCount={value.length} />
        </S.TextCountWrapper>
      </S.FieldWrapper>
    </S.Container>
  );
};

export default MainText;
