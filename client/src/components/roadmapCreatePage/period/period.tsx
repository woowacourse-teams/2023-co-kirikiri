import { PERIOD } from '@/constants/roadmap/regex';
import { useValidateInput } from '@/hooks/_common/useValidateInput';
import InputDescription from '../input/inputDescription/inputDescription';
import InputField from '../input/inputField/inputField';
import InputLabel from '../input/inputLabel/inputLebel';
import * as S from './period.styles';

const Period = () => {
  const { controlInputChange, validateInput, errorMessage, resetErrorMessage, value } =
    useValidateInput(PERIOD);
  return (
    <S.Container>
      <InputLabel text='예상기간' />
      <InputDescription text='컨텐츠를 달성하는데 걸리는 기간을 입력해주세요' />
      <S.FieldWrapper>
        <InputField
          handleInputChange={controlInputChange}
          maxLength={4}
          validateInput={validateInput}
          resetErrorMessage={resetErrorMessage}
          value={value}
        />
        <p>일</p>
      </S.FieldWrapper>
      <S.ErrorMessage>{errorMessage}</S.ErrorMessage>
    </S.Container>
  );
};

export default Period;
