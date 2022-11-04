export type Promisable<T> = Promise<T> | T

export type PartialRecord<KEY extends keyof any, VALUE> = Partial<Record<KEY, VALUE>>;
