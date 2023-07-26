declare const tag: unique symbol;
declare type InvariantProperty<T> = (arg: T) => T;
declare type InvariantSignature<T> = {
  readonly [tag]: InvariantProperty<T>;
};
export type InvariantOf<T> = T & InvariantSignature<T>;

export function invariantOf<T>(value: T): InvariantOf<T> {
  return value as InvariantOf<T>;
}

export function getInvariantObjectKeys<T>(arg: InvariantOf<T>): (keyof T)[] {
  return Object.keys(arg) as unknown as (keyof T)[];
}
