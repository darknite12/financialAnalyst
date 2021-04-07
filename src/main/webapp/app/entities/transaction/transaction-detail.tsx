import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './transaction.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface ITransactionDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const TransactionDetail = (props: ITransactionDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { transactionEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="transactionDetailsHeading">Transaction</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{transactionEntity.id}</dd>
          <dt>
            <span id="accountType">Account Type</span>
          </dt>
          <dd>{transactionEntity.accountType}</dd>
          <dt>
            <span id="transactionDate">Transaction Date</span>
          </dt>
          <dd>
            {transactionEntity.transactionDate ? (
              <TextFormat value={transactionEntity.transactionDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="chequeNumber">Cheque Number</span>
          </dt>
          <dd>{transactionEntity.chequeNumber}</dd>
          <dt>
            <span id="description1">Description 1</span>
          </dt>
          <dd>{transactionEntity.description1}</dd>
          <dt>
            <span id="description2">Description 2</span>
          </dt>
          <dd>{transactionEntity.description2}</dd>
          <dt>
            <span id="amountCAD">Amount CAD</span>
          </dt>
          <dd>{transactionEntity.amountCAD}</dd>
          <dt>
            <span id="amountUSD">Amount USD</span>
          </dt>
          <dd>{transactionEntity.amountUSD}</dd>
          <dt>
            <span id="isTracked">Is Tracked</span>
          </dt>
          <dd>{transactionEntity.isTracked ? 'true' : 'false'}</dd>
          <dt>Income</dt>
          <dd>{transactionEntity.income ? transactionEntity.income.id : ''}</dd>
          <dt>Expense</dt>
          <dd>{transactionEntity.expense ? transactionEntity.expense.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/transaction" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/transaction/${transactionEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ transaction }: IRootState) => ({
  transactionEntity: transaction.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(TransactionDetail);
