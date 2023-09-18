import { useCreateGoalRoom } from '@hooks/queries/goalRoom';
import useFormInput from '@hooks/_common/useFormInput';
import { CreateGoalRoomRequest } from '@myTypes/goalRoom/remote';
import PageSection from '../pageSection/PageSection';
import * as S from './CreateGoalRoomForm.styles';
import { convertFieldsToNumber } from '@utils/_common/convertFieldsToNumber';
import { NodeType } from '@myTypes/roadmap/internal';
import InputField from '@components/_common/InputField/InputField';
// import TodoListSection from '../todoListSection/TodoListSection';
import NodeSection from '../nodeSection/NodeSection';
import { transformDateStringsIn } from '@utils/_common/transformDateStringsIn';
import { generateNodesValidations, staticValidations } from './createGoalRoomValidations';

type CreateGoalRoomFormProps = {
  roadmapContentId: number;
  nodes: NodeType[];
};

const CreateGoalRoomForm = ({ roadmapContentId, nodes }: CreateGoalRoomFormProps) => {
  const { createGoalRoom } = useCreateGoalRoom(roadmapContentId);
  const { formState, handleInputChange, handleSubmit, error } =
    useFormInput<CreateGoalRoomRequest>(
      {
        roadmapContentId: Number(roadmapContentId),
        name: '',
        limitedMemberCount: 10,
        goalRoomRoadmapNodeRequests: nodes.map(({ id }) => ({
          roadmapNodeId: id,
          checkCount: 1,
          startDate: '',
          endDate: '',
        })),
      },
      {
        ...generateNodesValidations(nodes),
        ...staticValidations,
      }
    );

  const onSubmit = () => {
    const numericalFormState = convertFieldsToNumber(formState, [
      'limitedMemberCount',
      'checkCount',
    ]);
    const dateFormattedFormState = transformDateStringsIn(numericalFormState);

    createGoalRoom(dateFormattedFormState as CreateGoalRoomRequest);
  };

  return (
    <S.Form onSubmit={handleSubmit(onSubmit)}>
      <InputField
        label='인원수'
        description='입장 가능한 최대 인원수를 입력해주세요'
        isRequired
        type='number'
        size='small'
        name='limitedMemberCount'
        value={String(formState.limitedMemberCount)}
        onChange={handleInputChange}
        errorMessage={error?.limitedMemberCount}
        style={{ marginBottom: '2rem' }}
      />
      <InputField
        label='골룸 이름'
        isRequired
        placeholder='골룸의 이름을 작성해주세요'
        name='name'
        value={formState.name}
        onChange={handleInputChange}
        errorMessage={error?.name}
      />
      <PageSection
        isRequired
        title='로드맵 일정 지정'
        description='단계별 로드맵의 수행 일정과 인증 횟수를 지정해주세요'
      >
        <NodeSection
          nodes={nodes}
          formState={formState}
          error={error}
          handleInputChange={handleInputChange}
        />
      </PageSection>
      <S.SubmitButtonWrapper>
        <button>생성하기</button>
      </S.SubmitButtonWrapper>
    </S.Form>
  );
};

export default CreateGoalRoomForm;
