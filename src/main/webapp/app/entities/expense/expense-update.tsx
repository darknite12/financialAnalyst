import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { getEntity, updateEntity, createEntity, reset } from './expense.reducer';
import { IExpense } from 'app/shared/model/expense.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IExpenseUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const ExpenseUpdate = (props: IExpenseUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { expenseEntity, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/expense' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...expenseEntity,
        ...values,
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
          <h2 id="financialAnalystApp.expense.home.createOrEditLabel" data-cy="ExpenseCreateUpdateHeading">
            Create or edit a Expense
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : expenseEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="expense-id">ID</Label>
                  <AvInput id="expense-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="categoryLabel" for="expense-category">
                  Category
                </Label>
                <AvField
                  id="expense-category"
                  data-cy="category"
                  type="text"
                  name="category"
                  validate={{
                    required: { value: true, errorMessage: 'This field is required.' },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="nameLabel" for="expense-name">
                  Name
                </Label>
                <AvField
                  id="expense-name"
                  data-cy="name"
                  type="text"
                  name="name"
                  validate={{
                    required: { value: true, errorMessage: 'This field is required.' },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="searchString1Label" for="expense-searchString1">
                  Search String 1
                </Label>
                <AvField id="expense-searchString1" data-cy="searchString1" type="text" name="searchString1" />
              </AvGroup>
              <AvGroup>
                <Label id="searchString2Label" for="expense-searchString2">
                  Search String 2
                </Label>
                <AvField id="expense-searchString2" data-cy="searchString2" type="text" name="searchString2" />
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/expense" replace color="info">
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
  expenseEntity: storeState.expense.entity,
  loading: storeState.expense.loading,
  updating: storeState.expense.updating,
  updateSuccess: storeState.expense.updateSuccess,
});

const mapDispatchToProps = {
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(ExpenseUpdate);
