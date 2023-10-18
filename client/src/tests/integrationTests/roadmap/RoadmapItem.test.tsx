import {
  screen,
  fireEvent,
  render as rtlRender,
  RenderOptions,
  RenderResult,
} from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import RoadmapItem from '@components/_common/roadmapItem/RoadmapItem';
import { RoadmapDetailType } from '@myTypes/roadmap/internal';
import theme from '@styles/theme';
import { ThemeProvider } from 'styled-components';
import { ReactElement } from 'react';

const render = (ui: ReactElement, options?: RenderOptions): RenderResult =>
  rtlRender(ui, {
    wrapper: (props) => <ThemeProvider theme={theme} {...props} />,
    ...options,
  });

jest.mock('react-router-dom', () => {
  const navigateMock = jest.fn();
  return {
    ...jest.requireActual('react-router-dom'),
    useNavigate: () => navigateMock,
  };
});

const navigateMock = jest.requireMock('react-router-dom').useNavigate();

describe('RoadmapItem 컴포넌트', () => {
  const mockItem: Omit<RoadmapDetailType, 'content'> = {
    roadmapId: 1,
    roadmapTitle: '테스트 로드맵',
    introduction: '로드맵 소개',
    category: { id: 1, name: 'IT' },
    difficulty: 'EASY',
    recommendedRoadmapPeriod: 30,
    tags: [
      { id: 1, name: '태그1' },
      { id: 2, name: '태그2' },
    ],
    creator: { id: 3, name: '생성자' },
  };

  const mockRoadmapId = 1;

  const componentToRender = <RoadmapItem item={mockItem} roadmapId={mockRoadmapId} />;

  beforeEach(() => {
    navigateMock.mockClear();
  });

  it('제목과 소개, 난이도 등이 정상적으로 렌더링되는지 확인', () => {
    render(componentToRender);
    expect(screen.getByLabelText('로드맵 제목')).toHaveTextContent('테스트 로드맵');
    expect(screen.getByLabelText('로드맵 소개')).toHaveTextContent('로드맵 소개');
    expect(screen.getByLabelText('로드맵 속성')).toBeInTheDocument();
  });

  it('버튼 클릭 시, 정상적으로 페이지 이동 함수가 호출되는지 확인', () => {
    render(componentToRender);
    fireEvent.click(screen.getByText('자세히 보기'));
    expect(navigateMock).toHaveBeenCalledWith(`/roadmap/${mockRoadmapId}`);
  });

  it('hasBorder가 false인 경우, "진행중인 모임 보기" 버튼이 렌더링되는지 확인', () => {
    const componentToRender = (
      <RoadmapItem item={mockItem} roadmapId={mockRoadmapId} hasBorder={false} />
    );
    render(componentToRender);

    expect(screen.getByText('진행중인 모임 보기')).toBeInTheDocument();
  });

  it('hasBorder가 false일 때, 버튼 클릭 시 올바른 페이지로 이동하는지 확인', () => {
    const componentToRender = (
      <RoadmapItem item={mockItem} roadmapId={mockRoadmapId} hasBorder={false} />
    );
    render(componentToRender);

    fireEvent.click(screen.getByText('진행중인 모임 보기'));
    expect(navigateMock).toHaveBeenCalledWith(`/roadmap/${mockRoadmapId}/goalroom-list`);
  });

  it('Hover 상태일 때, HoverDescription이 보이는지 확인', () => {
    render(componentToRender);

    const description = screen.getByLabelText('로드맵 소개');
    fireEvent.mouseOver(description);

    expect(screen.getByLabelText('로드맵 소개-description')).toBeInTheDocument();
  });
});
