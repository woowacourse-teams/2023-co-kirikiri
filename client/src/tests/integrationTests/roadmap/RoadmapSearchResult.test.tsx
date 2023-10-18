import { screen, render } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import RoadmapSearchResult from '@components/roadmapListPage/roadmapSearch/RoadmapSearchResult';
import { MemoryRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ThemeProvider } from 'styled-components';
import theme from '@styles/theme';
import { useSearchRoadmapList } from '@hooks/queries/roadmap';

describe('RoadmapSearchResult 컴포넌트', () => {
  const queryClient = new QueryClient();

  const mockRoadmapList = [
    {
      roadmapId: 1,
      roadmapTitle: '테스트 로드맵1',
      introduction: '로드맵 소개',
      category: { id: 1, name: 'IT' },
      difficulty: 'EASY',
      recommendedRoadmapPeriod: 30,
      tags: [
        { id: 1, name: '태그1' },
        { id: 2, name: '태그2' },
      ],
      creator: { id: 3, name: '생성자' },
    },
    {
      roadmapId: 2,
      roadmapTitle: '테스트 로드맵2',
      introduction: '로드맵 소개',
      category: { id: 1, name: 'IT' },
      difficulty: 'EASY',
      recommendedRoadmapPeriod: 30,
      tags: [
        { id: 1, name: '태그1' },
        { id: 2, name: '태그2' },
      ],
      creator: { id: 3, name: '생성자' },
    },
  ];

  jest.mock('@/hooks/queries/roadmap', () => ({
    useSearchRoadmapList: jest.fn(() => ({
      searchRoadmapListResponse: {
        hasNext: true, // hasNext 값 설정
        responses: mockRoadmapList, // 모킹할 roadmapList 데이터 설정
      },
      fetchNextPage: jest.fn(), // fetchNextPage 함수도 모킹합니다.
    })),
  }));

  jest.spyOn({ useSearchRoadmapList }, 'useSearchRoadmapList').mockImplementation(() => ({
    searchRoadmapListResponse: {
      hasNext: true,
      responses: mockRoadmapList as any,
    },
    fetchNextPage: jest.fn(),
  }));

  beforeEach(() => {
    jest.clearAllMocks();
  });

  const componentToReder = (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider theme={theme}>
        <MemoryRouter>
          <RoadmapSearchResult />
        </MemoryRouter>
      </ThemeProvider>
    </QueryClientProvider>
  );

  it('로드맵 검색 결과가 없을 때 "NoResult" 컴포넌트를 렌더링하는지 확인.', () => {
    render(componentToReder);

    expect(screen.getByText('검색결과가 존재하지 않습니다. 😭')).toBeInTheDocument();
  });
});
