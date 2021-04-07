import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IIncome, defaultValue } from 'app/shared/model/income.model';

export const ACTION_TYPES = {
  FETCH_INCOME_LIST: 'income/FETCH_INCOME_LIST',
  FETCH_INCOME: 'income/FETCH_INCOME',
  CREATE_INCOME: 'income/CREATE_INCOME',
  UPDATE_INCOME: 'income/UPDATE_INCOME',
  PARTIAL_UPDATE_INCOME: 'income/PARTIAL_UPDATE_INCOME',
  DELETE_INCOME: 'income/DELETE_INCOME',
  RESET: 'income/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IIncome>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

export type IncomeState = Readonly<typeof initialState>;

// Reducer

export default (state: IncomeState = initialState, action): IncomeState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_INCOME_LIST):
    case REQUEST(ACTION_TYPES.FETCH_INCOME):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_INCOME):
    case REQUEST(ACTION_TYPES.UPDATE_INCOME):
    case REQUEST(ACTION_TYPES.DELETE_INCOME):
    case REQUEST(ACTION_TYPES.PARTIAL_UPDATE_INCOME):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.FETCH_INCOME_LIST):
    case FAILURE(ACTION_TYPES.FETCH_INCOME):
    case FAILURE(ACTION_TYPES.CREATE_INCOME):
    case FAILURE(ACTION_TYPES.UPDATE_INCOME):
    case FAILURE(ACTION_TYPES.PARTIAL_UPDATE_INCOME):
    case FAILURE(ACTION_TYPES.DELETE_INCOME):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.FETCH_INCOME_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
        totalItems: parseInt(action.payload.headers['x-total-count'], 10),
      };
    case SUCCESS(ACTION_TYPES.FETCH_INCOME):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_INCOME):
    case SUCCESS(ACTION_TYPES.UPDATE_INCOME):
    case SUCCESS(ACTION_TYPES.PARTIAL_UPDATE_INCOME):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_INCOME):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {},
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState,
      };
    default:
      return state;
  }
};

const apiUrl = 'api/incomes';

// Actions

export const getEntities: ICrudGetAllAction<IIncome> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_INCOME_LIST,
    payload: axios.get<IIncome>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`),
  };
};

export const getEntity: ICrudGetAction<IIncome> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_INCOME,
    payload: axios.get<IIncome>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<IIncome> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_INCOME,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IIncome> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_INCOME,
    payload: axios.put(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const partialUpdate: ICrudPutAction<IIncome> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.PARTIAL_UPDATE_INCOME,
    payload: axios.patch(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IIncome> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_INCOME,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
