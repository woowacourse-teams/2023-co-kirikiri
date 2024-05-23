import { render, act } from '@testing-library/react';
import { useInfiniteScroll } from '../../hooks/_common/useInfiniteScroll';

const DummyComponent = ({
  hasNextPage,
  fetchNextPage,
}: {
  hasNextPage: boolean;
  fetchNextPage: () => void;
}) => {
  const ref = useInfiniteScroll({ hasNextPage, fetchNextPage });
  return <div ref={ref}>Load more</div>;
};

describe('useInfiniteScroll 훅', () => {
  let observerCallback: (entries: any[]) => void;

  beforeAll(() => {
    global.IntersectionObserver = jest.fn().mockImplementation((callback) => {
      observerCallback = callback;
      return {
        observe: jest.fn(),
        unobserve: jest.fn(),
        disconnect: jest.fn(),
      };
    });
  });

  it('hasNextPage가 true이고, 교차점에 도달하면 fetchNextPage가 호출됨', () => {
    const fetchNextPage = jest.fn();
    const { container } = render(
      <DummyComponent hasNextPage fetchNextPage={fetchNextPage} />
    );

    act(() => {
      observerCallback([{ isIntersecting: true }]);
    });

    expect(fetchNextPage).toHaveBeenCalled();
  });

  it('hasNextPage가 false이면 fetchNextPage가 호출되지 않음', () => {
    const fetchNextPage = jest.fn();
    const { container } = render(
      <DummyComponent hasNextPage={false} fetchNextPage={fetchNextPage} />
    );

    act(() => {
      observerCallback([{ isIntersecting: true }]);
    });

    expect(fetchNextPage).not.toHaveBeenCalled();
  });

  it('교차점에 도달하지 않았을 경우 fetchNextPage가 호출되지 않음', () => {
    const fetchNextPage = jest.fn();
    const { container } = render(
      <DummyComponent hasNextPage fetchNextPage={fetchNextPage} />
    );

    act(() => {
      observerCallback([{ isIntersecting: false }]);
    });

    expect(fetchNextPage).not.toHaveBeenCalled();
  });

  it('hasNextPage가 true이고, 교차점에 도달하지 않으면 fetchNextPage가 호출되지 않음', () => {
    const fetchNextPage = jest.fn();
    render(<DummyComponent hasNextPage fetchNextPage={fetchNextPage} />);

    act(() => {
      observerCallback([{ isIntersecting: false }]);
    });

    expect(fetchNextPage).not.toHaveBeenCalled();
  });

  it('hasNextPage가 false이고, 교차점에 도달하면 fetchNextPage가 호출되지 않음', () => {
    const fetchNextPage = jest.fn();
    render(<DummyComponent hasNextPage={false} fetchNextPage={fetchNextPage} />);

    act(() => {
      observerCallback([{ isIntersecting: true }]);
    });

    expect(fetchNextPage).not.toHaveBeenCalled();
  });

  it('hasNextPage가 false이고, 교차점에 도달하지 않으면 fetchNextPage가 호출되지 않음', () => {
    const fetchNextPage = jest.fn();
    render(<DummyComponent hasNextPage={false} fetchNextPage={fetchNextPage} />);

    act(() => {
      observerCallback([{ isIntersecting: false }]);
    });

    expect(fetchNextPage).not.toHaveBeenCalled();
  });
});
