import { FormEvent } from 'react';
import { useCreateGoalRoom } from '@hooks/queries/goalRoom';
import useFormInput from '@hooks/_common/useFormInput';
import { CreateGoalRoomRequest } from '@myTypes/goalRoom/remote';
import InputField from '../inputField/InputField';
import PageSection from '../pageSection/PageSection';
import * as S from './CreateGoalRoomForm.styles';
import { convertFieldsToNumber } from '@utils/_common/convertFieldsToNumber';

// 로드맵 노드 데이터
const NODES = [
  {
    roadmapNodeId: 1,
    title: '1. JS 기본 타입',
    content: '로드맵 1주차에는 알고리즘을 배울거에요.',
  },
  {
    roadmapNodeId: 2,
    title: '2. 기본 연산',
    content: '로드맵 1주차에는 알고리즘을 배울거에요.',
  },
  {
    roadmapNodeId: 3,
    title: '2. 기본 연산',
    content: '로드맵 1주차에는 알고리즘을 배울거에요.',
  },
  {
    roadmapNodeId: 4,
    title: '2. 기본 연산',
    content: '로드맵 1주차에는 알고리즘을 배울거에요.',
  },
];

type CreateGoalRoomFormProps = {
  roadmapContentId: number;
};

const CreateGoalRoomForm = ({ roadmapContentId }: CreateGoalRoomFormProps) => {
  const { createGoalRoom } = useCreateGoalRoom();
  const { formState, handleInputChange } = useFormInput<CreateGoalRoomRequest>({
    roadmapContentId: Number(roadmapContentId),
    name: '',
    limitedMemberCount: 10,
    goalRoomTodo: {
      content: '',
      startDate: '',
      endDate: '',
    },
    goalRoomRoadmapNodeRequests: NODES.map(({ roadmapNodeId }) => ({
      roadmapNodeId,
      checkCount: 5,
      startDate: '',
      endDate: '',
    })),
  });

  const handleSubmit = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    const transformedFormState = convertFieldsToNumber(formState, [
      'limitedMemberCount',
      'checkCount',
    ]);

    createGoalRoom(transformedFormState as CreateGoalRoomRequest);
  };

  return (
    <S.Form onSubmit={handleSubmit}>
      <InputField label='골룸명' isRequired>
        <S.Input id='name' name='name' onChange={handleInputChange} />
      </InputField>
      <PageSection
        isRequired
        title='로드맵 일정 지정'
        description='단계별 로드맵의 수행 일정과 일증 횟수를 지정해주세요'
      >
        <S.NodeSectionWrapper>
          <S.NodeList nodeCount={NODES.length}>
            {NODES.map(({ roadmapNodeId, title }) => (
              <S.NodeWrapper key={roadmapNodeId}>
                <S.NodeInfo>{title}</S.NodeInfo>
                <S.NodeConfigs>
                  <>
                    <InputField label='수행 시작 일자' isRequired type='small'>
                      <S.DateInput
                        id='수행 시작 일자'
                        name={`goalRoomRoadmapNodeRequests[${
                          roadmapNodeId - 1
                        }][startDate]`}
                        onChange={handleInputChange}
                        placeholder='2023-08-12'
                      />
                    </InputField>
                    <InputField label='수행 종료 일자' isRequired type='small'>
                      <S.DateInput
                        id='수행 종료 일자'
                        name={`goalRoomRoadmapNodeRequests[${
                          roadmapNodeId - 1
                        }][endDate]`}
                        onChange={handleInputChange}
                        placeholder='2023-08-13'
                      />
                    </InputField>
                  </>
                  <InputField label='인증 횟수' isRequired type='small'>
                    <S.Input
                      name={`goalRoomRoadmapNodeRequests[${
                        roadmapNodeId - 1
                      }][checkCount]`}
                      onChange={handleInputChange}
                    />
                  </InputField>
                </S.NodeConfigs>
              </S.NodeWrapper>
            ))}
          </S.NodeList>
        </S.NodeSectionWrapper>
      </PageSection>
      <InputField label='투두리스트 생성'>
        <div>
          <InputField label='수행 시작 일자' isRequired type='small'>
            <S.DateInput
              id='수행 시작 일자'
              placeholder='2023-08-12'
              name='goalRoomTodo[startDate]'
              onChange={handleInputChange}
            />
          </InputField>
          <InputField label='수행 종료 일자' isRequired type='small'>
            <S.DateInput
              id='수행 종료 일자'
              placeholder='2023-08-13'
              name='goalRoomTodo[endDate]'
              onChange={handleInputChange}
            />
          </InputField>
        </div>
        <S.Textarea
          id='투두리스트 생성'
          name='goalRoomTodo[content]'
          onChange={handleInputChange}
        />
      </InputField>
      <button>생성하기</button>
    </S.Form>
  );
};

export default CreateGoalRoomForm;
