import React, { useState, useEffect } from 'react';
import InfiniteScroll from 'react-infinite-scroller';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Col, Row, Table } from 'reactstrap';
import { Translate, TextFormat, getSortState, IPaginationBaseState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntities, reset } from './transaction.reducer';
import { ITransaction } from 'app/shared/model/transaction.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';

export interface ITransactionProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export const Transaction = (props: ITransactionProps) => {
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getSortState(props.location, ITEMS_PER_PAGE, 'id'), props.location.search)
  );
  const [sorting, setSorting] = useState(false);

  const getAllEntities = () => {
    props.getEntities(paginationState.activePage - 1, paginationState.itemsPerPage, `${paginationState.sort},${paginationState.order}`);
  };

  const resetAll = () => {
    props.reset();
    setPaginationState({
      ...paginationState,
      activePage: 1,
    });
    props.getEntities();
  };

  useEffect(() => {
    resetAll();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      resetAll();
    }
  }, [props.updateSuccess]);

  useEffect(() => {
    getAllEntities();
  }, [paginationState.activePage]);

  const handleLoadMore = () => {
    if ((window as any).pageYOffset > 0) {
      setPaginationState({
        ...paginationState,
        activePage: paginationState.activePage + 1,
      });
    }
  };

  useEffect(() => {
    if (sorting) {
      getAllEntities();
      setSorting(false);
    }
  }, [sorting]);

  const sort = p => () => {
    props.reset();
    setPaginationState({
      ...paginationState,
      activePage: 1,
      order: paginationState.order === 'asc' ? 'desc' : 'asc',
      sort: p,
    });
    setSorting(true);
  };

  const handleSyncList = () => {
    resetAll();
  };

  const { transactionList, match, loading } = props;
  return (
    <div>
      <h2 id="transaction-heading" data-cy="TransactionHeading">
        Transactions
        <div className="d-flex justify-content-end">
          <Button className="mr-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh List
          </Button>
          <Link to={`${match.url}/new`} className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create new Transaction
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        <InfiniteScroll
          pageStart={paginationState.activePage}
          loadMore={handleLoadMore}
          hasMore={paginationState.activePage - 1 < props.links.next}
          loader={<div className="loader">Loading ...</div>}
          threshold={0}
          initialLoad={false}
        >
          {transactionList && transactionList.length > 0 ? (
            <Table responsive>
              <thead>
                <tr>
                  <th className="hand" onClick={sort('id')}>
                    ID <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={sort('accountType')}>
                    Account Type <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={sort('transactionDate')}>
                    Transaction Date <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={sort('chequeNumber')}>
                    Cheque Number <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={sort('description1')}>
                    Description 1 <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={sort('description2')}>
                    Description 2 <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={sort('amountCAD')}>
                    Amount CAD <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={sort('amountUSD')}>
                    Amount USD <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={sort('isTracked')}>
                    Is Tracked <FontAwesomeIcon icon="sort" />
                  </th>
                  <th>
                    Income <FontAwesomeIcon icon="sort" />
                  </th>
                  <th>
                    Expense <FontAwesomeIcon icon="sort" />
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {transactionList.map((transaction, i) => (
                  <tr key={`entity-${i}`} data-cy="entityTable">
                    <td>
                      <Button tag={Link} to={`${match.url}/${transaction.id}`} color="link" size="sm">
                        {transaction.id}
                      </Button>
                    </td>
                    <td>{transaction.accountType}</td>
                    <td>
                      {transaction.transactionDate ? (
                        <TextFormat type="date" value={transaction.transactionDate} format={APP_DATE_FORMAT} />
                      ) : null}
                    </td>
                    <td>{transaction.chequeNumber}</td>
                    <td>{transaction.description1}</td>
                    <td>{transaction.description2}</td>
                    <td>{transaction.amountCAD}</td>
                    <td>{transaction.amountUSD}</td>
                    <td>{transaction.isTracked ? 'true' : 'false'}</td>
                    <td>{transaction.income ? <Link to={`income/${transaction.income.id}`}>{transaction.income.id}</Link> : ''}</td>
                    <td>{transaction.expense ? <Link to={`expense/${transaction.expense.id}`}>{transaction.expense.id}</Link> : ''}</td>
                    <td className="text-right">
                      <div className="btn-group flex-btn-group-container">
                        <Button tag={Link} to={`${match.url}/${transaction.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                          <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                        </Button>
                        <Button tag={Link} to={`${match.url}/${transaction.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                        </Button>
                        <Button
                          tag={Link}
                          to={`${match.url}/${transaction.id}/delete`}
                          color="danger"
                          size="sm"
                          data-cy="entityDeleteButton"
                        >
                          <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Delete</span>
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          ) : (
            !loading && <div className="alert alert-warning">No Transactions found</div>
          )}
        </InfiniteScroll>
      </div>
    </div>
  );
};

const mapStateToProps = ({ transaction }: IRootState) => ({
  transactionList: transaction.entities,
  loading: transaction.loading,
  totalItems: transaction.totalItems,
  links: transaction.links,
  entity: transaction.entity,
  updateSuccess: transaction.updateSuccess,
});

const mapDispatchToProps = {
  getEntities,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(Transaction);
