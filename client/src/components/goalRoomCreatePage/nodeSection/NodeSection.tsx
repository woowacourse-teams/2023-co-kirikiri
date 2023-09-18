import { CreateGoalRoomRequest } from '@myTypes/goalRoom/remote';
import InputField from '@components/_common/InputField/InputField';
import { HandleInputChangeType, FormErrorType } from '@hooks/_common/useFormInput';
import { NodeType } from '@myTypes/roadmap/internal';
import * as S from './NodeSection.styles';
// import ToolTip from '@/components/_common/toolTip/ToolTip';

type NodeSectionProps = {
  nodes: NodeType[];
  formState: CreateGoalRoomRequest;
  handleInputChange: HandleInputChangeType;
  error: FormErrorType;
};

const NodeSection = (props: NodeSectionProps) => {
  const { nodes, formState, handleInputChange, error } = props;

  return (
    <S.NodeList>
      {nodes.map(({ id, title, description }, index) => (
        <S.Node key={id}>
          <S.NodeInfo>
            <S.NodeTitle>{title}</S.NodeTitle>
            <S.NodeDescription>{description}</S.NodeDescription>
          </S.NodeInfo>
          <S.NodeConfigs>
            <S.DateConfig>
              <InputField
                label='수행 시작 일자'
                isRequired
                type='date'
                size='small'
                name={`goalRoomRoadmapNodeRequests[${index}][startDate]`}
                value={formState.goalRoomRoadmapNodeRequests[index].startDate}
                onChange={handleInputChange}
                errorMessage={error?.[`goalRoomRoadmapNodeRequests[${index}][startDate]`]}
              />
              <InputField
                label='수행 종료 일자'
                isRequired
                type='date'
                size='small'
                name={`goalRoomRoadmapNodeRequests[${index}][endDate]`}
                value={formState.goalRoomRoadmapNodeRequests[index].endDate}
                onChange={handleInputChange}
                errorMessage={error?.[`goalRoomRoadmapNodeRequests[${index}][endDate]`]}
              />
            </S.DateConfig>
            <InputField
              label='인증 횟수'
              isRequired
              size='small'
              type='number'
              name={`goalRoomRoadmapNodeRequests[${index}][checkCount]`}
              value={String(formState.goalRoomRoadmapNodeRequests[index].checkCount)}
              onChange={handleInputChange}
              errorMessage={error?.[`goalRoomRoadmapNodeRequests[${index}][checkCount]`]}
              toolTip={
                <>
                  해당 <strong>단계</strong>에서 활동을 <strong>인증할 횟수</strong>를
                  입력해주세요
                </>
              }
            />
          </S.NodeConfigs>
        </S.Node>
      ))}
    </S.NodeList>
  );
};

export default NodeSection;
