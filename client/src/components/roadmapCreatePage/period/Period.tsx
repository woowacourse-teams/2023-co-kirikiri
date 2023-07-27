import { PERIOD } from '@/constants/roadmap/regex';
import { useValidateInput } from '@/hooks/_common/useValidateInput';
import InputDescription from '../input/inputDescription/InputDescription';
import InputField from '../input/inputField/InputField';
import InputLabel from '../input/inputLabel/InputLebel';
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
          name='requiredPeriod'
          data-valid={validateInput}
        />
        <p>일</p>
      </S.FieldWrapper>
      <S.ErrorMessage>{errorMessage}</S.ErrorMessage>
    </S.Container>
  );
};

export default Period;
