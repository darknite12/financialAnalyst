import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IIncome } from 'app/shared/model/income.model';
import { getEntities as getIncomes } from 'app/entities/income/income.reducer';
import { IExpense } from 'app/shared/model/expense.model';
import { getEntities as getExpenses } from 'app/entities/expense/expense.reducer';
import { getEntity, updateEntity, createEntity, reset } from './transaction.reducer';
import { ITransaction } from 'app/shared/model/transaction.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface ITransactionUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const TransactionUpdate = (props: ITransactionUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { transactionEntity, incomes, expenses, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/transaction');
  };

  useEffect(() => {
    if (!isNew) {
      props.getEntity(props.match.params.id);
    }

    props.getIncomes();
    props.getExpenses();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    values.transactionDate = convertDateTimeToServer(values.transactionDate);

    if (errors.length === 0) {
      const entity = {
        ...transactionEntity,
        ...values,
        income: incomes.find(it => it.id.toString() === values.incomeId.toString()),
        expense: expenses.find(it => it.id.toString() === values.expenseId.toString()),
      };

      if (isNew) {
        props.createEntity(entity);
      } else {
        props.updateEntity(entity);
      }
    }
  };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="financialAnalystApp.transaction.home.createOrEditLabel" data-cy="TransactionCreateUpdateHeading">
            Create or edit a Transaction
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : transactionEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="transaction-id">ID</Label>
                  <AvInput id="transaction-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="accountTypeLabel" for="transaction-accountType">
                  Account Type
                </Label>
                <AvField id="transaction-accountType" data-cy="accountType" type="text" name="accountType" />
              </AvGroup>
              <AvGroup>
                <Label id="transactionDateLabel" for="transaction-transactionDate">
                  Transaction Date
                </Label>
                <AvInput
                  id="transaction-transactionDate"
                  data-cy="transactionDate"
                  type="datetime-local"
                  className="form-control"
                  name="transactionDate"
                  placeholder={'YYYY-MM-DD HH:mm'}
                  value={isNew ? displayDefaultDateTime() : convertDateTimeFromServer(props.transactionEntity.transactionDate)}
                />
              </AvGroup>
              <AvGroup>
                <Label id="chequeNumberLabel" for="transaction-chequeNumber">
                  Cheque Number
                </Label>
                <AvField id="transaction-chequeNumber" data-cy="chequeNumber" type="text" name="chequeNumber" />
              </AvGroup>
              <AvGroup>
                <Label id="description1Label" for="transaction-description1">
                  Description 1
                </Label>
                <AvField id="transaction-description1" data-cy="description1" type="text" name="description1" />
              </AvGroup>
              <AvGroup>
                <Label id="description2Label" for="transaction-description2">
                  Description 2
                </Label>
                <AvField id="transaction-description2" data-cy="description2" type="text" name="description2" />
              </AvGroup>
              <AvGroup>
                <Label id="amountCADLabel" for="transaction-amountCAD">
                  Amount CAD
                </Label>
                <AvField id="transaction-amountCAD" data-cy="amountCAD" type="string" className="form-control" name="amountCAD" />
              </AvGroup>
              <AvGroup>
                <Label id="amountUSDLabel" for="transaction-amountUSD">
                  Amount USD
                </Label>
                <AvField id="transaction-amountUSD" data-cy="amountUSD" type="string" className="form-control" name="amountUSD" />
              </AvGroup>
              <AvGroup check>
                <Label id="isTrackedLabel">
                  <AvInput id="transaction-isTracked" data-cy="isTracked" type="checkbox" className="form-check-input" name="isTracked" />
                  Is Tracked
                </Label>
              </AvGroup>
              <AvGroup>
                <Label for="transaction-income">Income</Label>
                <AvInput id="transaction-income" data-cy="income" type="select" className="form-control" name="incomeId">
                  <option value="" key="0" />
                  {incomes
                    ? incomes.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label for="transaction-expense">Expense</Label>
                <AvInput id="transaction-expense" data-cy="expense" type="select" className="form-control" name="expenseId">
                  <option value="" key="0" />
                  {expenses
                    ? expenses.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/transaction" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </AvForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  incomes: storeState.income.entities,
  expenses: storeState.expense.entities,
  transactionEntity: storeState.transaction.entity,
  loading: storeState.transaction.loading,
  updating: storeState.transaction.updating,
  updateSuccess: storeState.transaction.updateSuccess,
});

const mapDispatchToProps = {
  getIncomes,
  getExpenses,
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(TransactionUpdate);
