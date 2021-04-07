import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Income from './income';
import IncomeDetail from './income-detail';
import IncomeUpdate from './income-update';
import IncomeDeleteDialog from './income-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={IncomeUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={IncomeUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={IncomeDetail} />
      <ErrorBoundaryRoute path={match.url} component={Income} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={IncomeDeleteDialog} />
  </>
);

export default Routes;
