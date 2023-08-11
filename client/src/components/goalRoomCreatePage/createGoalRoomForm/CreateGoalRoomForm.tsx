import { useCreateGoalRoom } from '@hooks/queries/goalRoom';
import useFormInput from '@hooks/_common/useFormInput';
import { CreateGoalRoomRequest } from '@myTypes/goalRoom/remote';
import PageSection from '../pageSection/PageSection';
import * as S from './CreateGoalRoomForm.styles';
import { convertFieldsToNumber } from '@utils/_common/convertFieldsToNumber';
import { NodeType } from '@myTypes/roadmap/internal';
import InputField from '@components/_common/InputField/InputField';
import TodoListSection from '../todoListSection/TodoListSection';
import NodeSection from '../nodeSection/NodeSection';
import { transformDateStringsIn } from '@utils/_common/transformDateStringsIn';

type CreateGoalRoomFormProps = {
  roadmapContentId: number;
  nodes: NodeType[];
};

const createGoalRoomValidation = {
  name: [
    {
      validate: (inputValue: string) => inputValue.length > 0,
      message: '이름은 필수 항목입니다',
      updateOnFail: true,
    },
  ],
  limitedMemberCount: [
    {
      validate: (inputValue: string) => inputValue.length > 0,
      message: '최대 인원수는 필수 항목입니다',
      updateOnFail: true,
    },
  ],
  'goalRoomTodo[content]': [
    {
      validate: (inputValue: string) => inputValue.length > 0,
      message: '투두 리스트는 필수 항목입니다',
      updateOnFail: true,
    },
    {
      validate: (inputValue: string) => inputValue.length <= 10,
      message: '최대 250글자까지 작성할 수 있습니다',
      updateOnFail: false,
    },
  ],
};

const CreateGoalRoomForm = ({ roadmapContentId, nodes }: CreateGoalRoomFormProps) => {
  const { createGoalRoom } = useCreateGoalRoom(roadmapContentId);
  const { formState, handleInputChange, handleSubmit, error } =
    useFormInput<CreateGoalRoomRequest>(
      {
        roadmapContentId: Number(roadmapContentId),
        name: '',
        limitedMemberCount: 10,
        goalRoomTodo: {
          content: '',
          startDate: '',
          endDate: '',
        },
        goalRoomRoadmapNodeRequests: nodes.map(({ id }) => ({
          roadmapNodeId: id,
          checkCount: 1,
          startDate: '',
          endDate: '',
        })),
      },
      createGoalRoomValidation
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
        description='단계별 로드맵의 수행 일정과 일증 횟수를 지정해주세요'
      >
        <NodeSection
          nodes={nodes}
          formState={formState}
          error={error}
          handleInputChange={handleInputChange}
        />
      </PageSection>
      <PageSection isRequired title='투두리스트 생성'>
        <TodoListSection
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
