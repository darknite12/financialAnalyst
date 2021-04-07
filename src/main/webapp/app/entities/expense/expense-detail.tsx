import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './expense.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IExpenseDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const ExpenseDetail = (props: IExpenseDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { expenseEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="expenseDetailsHeading">Expense</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{expenseEntity.id}</dd>
          <dt>
            <span id="category">Category</span>
          </dt>
          <dd>{expenseEntity.category}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{expenseEntity.name}</dd>
          <dt>
            <span id="searchString1">Search String 1</span>
          </dt>
          <dd>{expenseEntity.searchString1}</dd>
          <dt>
            <span id="searchString2">Search String 2</span>
          </dt>
          <dd>{expenseEntity.searchString2}</dd>
        </dl>
        <Button tag={Link} to="/expense" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/expense/${expenseEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ expense }: IRootState) => ({
  expenseEntity: expense.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(ExpenseDetail);
