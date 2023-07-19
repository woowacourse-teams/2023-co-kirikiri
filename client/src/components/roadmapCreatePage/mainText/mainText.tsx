import { useValidateInput } from '@/hooks/_common/useValidateInput';
import InputField from '../input/inputField/inputField';
import InputLabel from '../input/inputLabel/inputLebel';
import TextCount from '../input/textCount/textCount';
import * as S from './mainText.styles';

const MAIN_TEXT_MAX_LENGTH = { rule: /^.{0,2000}$/, message: '' };
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
        />
        <S.TextCountWrapper>
          <TextCount maxCount={2000} currentCount={value.length} />
        </S.TextCountWrapper>
      </S.FieldWrapper>
    </S.Container>
  );
};

export default MainText;
