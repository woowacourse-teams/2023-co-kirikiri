// Object.keys()로 배열을 만들었을 때 객체의 키를 가지고 객체에 접근할 때 타입추론이 되지 않는 문제를 해결하는 util함수

// 고유하고 변경 불가능한 심볼을 생성
declare const tag: unique symbol;

// T타입의 인자를 받으면 무조건 T타입은 반환하는 InvariantProperty<T> 생성
declare type InvariantProperty<T> = (arg: T) => T;

// 인자로 전달된 타입의 프로퍼티로 tag라는 고유한 심볼을 가지고 있는 읽기 전용 객체 타입 생성
declare type InvariantSignature<T> = {
  readonly [tag]: InvariantProperty<T>;
};

// 인자로 전달된 타입에 InvariantSignature 타입을 추가함으로써, tag 프로퍼티를 가지는 불변 객체 생성
export type InvariantOf<T> = T & InvariantSignature<T>;

// 제네릭 타입을 받아 해당 타입을 무공변적 타입으로 변환하는 함수
export function invariantOf<T>(value: T): InvariantOf<T> {
  return value as InvariantOf<T>;
}

// 이 함수의 인자로 원하는 객체를 invariantOf함수로 감싸서 넣어주면 객체의 타입을 추론 가능
export function getInvariantObjectKeys<T>(arg: InvariantOf<T>): (keyof T)[] {
  return Object.keys(arg) as unknown as (keyof T)[];
}
