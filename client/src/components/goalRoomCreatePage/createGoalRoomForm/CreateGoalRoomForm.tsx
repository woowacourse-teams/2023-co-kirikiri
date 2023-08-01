import InputField from '../inputField/InputField';
import PageSection from '../pageSection/PageSection';
import { Stepper } from '../stepper/Stepper';
import * as S from './CreateGoalRoomForm.styles';

const NODES = [
  {
    title: '1. JS 기본 타입',
    content: '로드맵 1주차에는 알고리즘을 배울거에요.',
  },
  {
    title: '2. 기본 연산',
    content: '로드맵 1주차에는 알고리즘을 배울거에요.',
  },
];

const CreateGoalRoomForm = () => {
  return (
    <S.Form>
      <InputField label='골룸명' isRequired>
        <S.Input id='골룸명' />
      </InputField>
      <PageSection
        isRequired
        title='로드맵 일정 지정'
        description='단계별 로드맵의 수행 일정과 일증 횟수를 지정해주세요'
      >
        <S.NodeSectionWrapper>
          <S.NodeList nodeCount={NODES.length}>
            {NODES.map(({ title }) => (
              <S.NodeWrapper key={title}>
                <S.NodeInfo>{title}</S.NodeInfo>
                <S.NodeConfigs>
                  <InputField label='수행 시작 일자' isRequired type='small'>
                    <S.DateInput id='수행 시작 일자' placeholder='2023-08-12' />
                  </InputField>
                  <InputField label='인증 횟수' isRequired type='small'>
                    <Stepper />
                  </InputField>
                </S.NodeConfigs>
              </S.NodeWrapper>
            ))}
          </S.NodeList>
        </S.NodeSectionWrapper>
      </PageSection>
      <InputField label='투두리스트 생성'>
        <S.Textarea id='투두리스트 생성' />
      </InputField>
    </S.Form>
  );
};

export default CreateGoalRoomForm;
