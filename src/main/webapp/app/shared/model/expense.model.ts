export interface IExpense {
  id?: number;
  category?: string;
  name?: string;
  searchString1?: string | null;
  searchString2?: string | null;
}

export const defaultValue: Readonly<IExpense> = {};
