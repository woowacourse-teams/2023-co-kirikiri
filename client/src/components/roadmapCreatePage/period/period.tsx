import { useValidateInput } from '@/hooks/_common/useValidateInput';
import InputDescription from '../input/inputDescription/inputDescription';
import InputField from '../input/inputField/inputField';
import InputLabel from '../input/inputLabel/inputLebel';
import * as S from './period.styles';

const PERIOD = {
  rule: /^(?:[1-9]\d{0,2}|1000)$/,
  message: '1일부터 100일까지만 입력할 수 있습니다',
};

const Period = () => {
  const { handleInputChange, checkBlank, errorMessage } = useValidateInput([PERIOD]);
  return (
    <S.Container>
      <InputLabel text='예상기간' />
      <InputDescription text='컨텐츠를 달성하는데 걸리는 기간을 입력해주세요' />
      <S.FieldWrapper>
        <InputField
          handleInputChange={handleInputChange}
          maxLength={4}
          checkBlank={checkBlank}
        />
        <p>일</p>
      </S.FieldWrapper>
      <S.ErrorMessage>{errorMessage}</S.ErrorMessage>
    </S.Container>
  );
};

export default Period;
