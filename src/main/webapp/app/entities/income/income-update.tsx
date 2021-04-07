import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { getEntity, updateEntity, createEntity, reset } from './income.reducer';
import { IIncome } from 'app/shared/model/income.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IIncomeUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const IncomeUpdate = (props: IIncomeUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { incomeEntity, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/income' + props.location.search);
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
        ...incomeEntity,
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
          <h2 id="financialAnalystApp.income.home.createOrEditLabel" data-cy="IncomeCreateUpdateHeading">
            Create or edit a Income
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : incomeEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="income-id">ID</Label>
                  <AvInput id="income-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="nameLabel" for="income-name">
                  Name
                </Label>
                <AvField
                  id="income-name"
                  data-cy="name"
                  type="text"
                  name="name"
                  validate={{
                    required: { value: true, errorMessage: 'This field is required.' },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="searchString1Label" for="income-searchString1">
                  Search String 1
                </Label>
                <AvField id="income-searchString1" data-cy="searchString1" type="text" name="searchString1" />
              </AvGroup>
              <AvGroup>
                <Label id="searchString2Label" for="income-searchString2">
                  Search String 2
                </Label>
                <AvField id="income-searchString2" data-cy="searchString2" type="text" name="searchString2" />
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/income" replace color="info">
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
  incomeEntity: storeState.income.entity,
  loading: storeState.income.loading,
  updating: storeState.income.updating,
  updateSuccess: storeState.income.updateSuccess,
});

const mapDispatchToProps = {
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(IncomeUpdate);
