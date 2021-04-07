import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Expense from './expense';
import ExpenseDetail from './expense-detail';
import ExpenseUpdate from './expense-update';
import ExpenseDeleteDialog from './expense-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ExpenseUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ExpenseUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ExpenseDetail} />
      <ErrorBoundaryRoute path={match.url} component={Expense} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ExpenseDeleteDialog} />
  </>
);

export default Routes;
