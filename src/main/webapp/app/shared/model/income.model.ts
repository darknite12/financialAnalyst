export interface IIncome {
  id?: number;
  name?: string;
  searchString1?: string | null;
  searchString2?: string | null;
}

export const defaultValue: Readonly<IIncome> = {};
