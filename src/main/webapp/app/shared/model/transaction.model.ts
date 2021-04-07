import dayjs from 'dayjs';
import { IIncome } from 'app/shared/model/income.model';
import { IExpense } from 'app/shared/model/expense.model';

export interface ITransaction {
  id?: number;
  accountType?: string | null;
  transactionDate?: string | null;
  chequeNumber?: string | null;
  description1?: string | null;
  description2?: string | null;
  amountCAD?: number | null;
  amountUSD?: number | null;
  isTracked?: boolean | null;
  income?: IIncome | null;
  expense?: IExpense | null;
}

export const defaultValue: Readonly<ITransaction> = {
  isTracked: false,
};
