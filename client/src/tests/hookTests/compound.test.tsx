import React from 'react';
import { render } from '@testing-library/react';
import { combineStates, getCustomElement } from '@hooks/_common/compound';

test('combineStates는 externalState만 호출하고 innerState는 null일 경우', () => {
  const mockExternalState = jest.fn();
  const params = { key: 'value' };

  const combinedState = combineStates(mockExternalState, null);

  combinedState(params);

  expect(mockExternalState).toHaveBeenCalledWith(params);
});

test('combineStates는 innerState만 호출하고 externalState는 null일 경우', () => {
  const mockInnerState = jest.fn();
  const params = { key: 'value' };

  const combinedState = combineStates(null, mockInnerState);

  combinedState(params);

  expect(mockInnerState).toHaveBeenCalledWith(params);
});

test('combineStates는 externalState와 innerState 둘 다 null일 경우', () => {
  const params = { key: 'value' };

  const combinedState = combineStates(null, null);

  // 이 경우에는 아무 것도 호출되지 않으므로, 별도의 expect가 필요하지 않을 수 있습니다.
  combinedState(params);
});

test('combineStates는 externalState와 innerState 모두 호출해야 한다', () => {
  const mockExternalState = jest.fn();
  const mockInnerState = jest.fn();
  const params = { key: 'value' };

  const combinedState = combineStates(mockExternalState, mockInnerState);

  combinedState(params);

  expect(mockExternalState).toHaveBeenCalledWith(params);
  expect(mockInnerState).toHaveBeenCalledWith(params);
});

test('getCustomElement는 유효한 React 요소를 새로운 props와 함께 복제하고 반환해야 한다', () => {
  const SampleComponent = (props: any) => <div {...props}>Hello</div>;
  const newProps = { className: 'new-class' };

  const customElement = getCustomElement(<SampleComponent />, newProps);
  const { container } = render(customElement);

  expect(container.firstChild).toHaveClass('new-class');
});

test('getCustomElement는 유효하지 않은 React 요소가 주어지면 에러를 던져야 한다', () => {
  expect(() => {
    getCustomElement('InvalidElement' as unknown as React.ReactElement, {});
  }).toThrowError('Invalid React Element!');
});

test('combineStates는 externalState만 호출하고 innerState는 null일 경우', () => {
  const mockExternalState = jest.fn();
  const params = { key: 'value' };

  const combinedState = combineStates(mockExternalState, null);

  combinedState(params);

  expect(mockExternalState).toHaveBeenCalledWith(params);
});

test('combineStates는 innerState만 호출하고 externalState는 undefined일 경우', () => {
  const mockInnerState = jest.fn();
  const params = { key: 'value' };

  const combinedState = combineStates(undefined, mockInnerState);

  combinedState(params);

  expect(mockInnerState).toHaveBeenCalledWith(params);
});
