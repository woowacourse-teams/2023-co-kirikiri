import { ROADMAP_MAX_LENGTH } from '@/constants/roadmap/regex';
import { useValidateInput } from '@/hooks/_common/useValidateInput';
import InputField from '../input/inputField/InputField';
import * as S from './roadmap.styles';

type RoadmapItemType = {
  roadmapNumber: number;
};

const RoadmapItem = ({ roadmapNumber }: RoadmapItemType) => {
  const { handleInputChange, validateInput, resetErrorMessage } =
    useValidateInput(ROADMAP_MAX_LENGTH);

  return (
    <>
      <S.TitleWrapper>
        <S.RoadmapNumber>{roadmapNumber}</S.RoadmapNumber>
        <S.TitleFieldWrapper>
          <InputField
            handleInputChange={handleInputChange}
            validateInput={validateInput}
            resetErrorMessage={resetErrorMessage}
            maxLength={40}
          />
        </S.TitleFieldWrapper>
      </S.TitleWrapper>
      <S.BodyFieldWrapper>
        <InputField
          handleInputChange={handleInputChange}
          validateInput={validateInput}
          resetErrorMessage={resetErrorMessage}
          maxLength={2000}
        />
      </S.BodyFieldWrapper>
    </>
  );
};

export default RoadmapItem;
