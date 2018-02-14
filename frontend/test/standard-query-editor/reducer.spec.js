// @flow

import { expect } from 'chai';

import {
  default as createQueryNodeModalActions
} from '../../lib/js/query-node-modal/actions';

import { default as reducer } from '../../lib/js/standard-query-editor/reducer';


const createQueryStateWithOneBigMultiSelect = () => ([{
  elements: [{
    id: "elm0",
    tables: [{
      id: "elm0.tbl0",
      filters: [{
        id: "col0",
        type: "BIG_MULTI_SELECT",
        options: []
      }]
    }],
    hasActiveFilters: false
  }]
}]);

describe('standard query editor', () => {
  describe('clearing a filter\'s value', () => {
    it('sets the filter value to undefined', () => {
      const state = createQueryStateWithOneBigMultiSelect();
      state[0].elements[0].tables[0].filters[0].options = [{value: 1, label: '1'}];
      state[0].elements[0].tables[0].filters[0].value = [1];

      const { setStandardFilterValue } = createQueryNodeModalActions('standard');
      const action = setStandardFilterValue(0, 0, 0, 0, []);
      const updatedState = reducer(state, action);

      expect(updatedState[0].elements[0].tables[0].filters[0].value).to.equal(undefined);
    });
  });

  describe('receiving a list of autocomplete suggestions', () => {
    it('updates the filter\'s options list', () => {
      const state = createQueryStateWithOneBigMultiSelect();

      const options = [
        { value: "0", label: "0" },
        { value: "1", label: "1" },
        { value: "2", label: "2" },
      ];
      const { loadStandardFilterSuggestionsSuccess } = createQueryNodeModalActions('standard');
      const action = loadStandardFilterSuggestionsSuccess(options, 0, 0, 0, 0);
      const updatedState = reducer(state, action);

      expect(updatedState[0].elements[0].tables[0].filters[0].options).to.deep.equal(options);
    });

    it('enhances the filter\'s options list with additional suggestions', () => {
      const options = [
        { value: "0", label: "0" },
        { value: "1", label: "1" },
        { value: "2", label: "2" },
      ];
      const state = createQueryStateWithOneBigMultiSelect();
      state[0].elements[0].tables[0].filters[0].options = options;

      const newOptions = [
        { value: "3", label: "3" },
        { value: "4", label: "4" },
        { value: "5", label: "5" },
      ];
      const { loadStandardFilterSuggestionsSuccess } = createQueryNodeModalActions('standard');
      const action = loadStandardFilterSuggestionsSuccess(newOptions, 0, 0, 0, 0);
      const updatedState = reducer(state, action);

      expect(updatedState[0].elements[0].tables[0].filters[0].options)
        .to.deep.equal([...newOptions, ...options]);
    });

    it('leaves filter\'s options list unmodified when receiving empty suggestions list', () => {
      const options = [
        { value: "0", label: "0" },
        { value: "1", label: "1" },
        { value: "2", label: "2" },
      ];
      const state = createQueryStateWithOneBigMultiSelect();
      state[0].elements[0].tables[0].filters[0].options = options;

      const newOptions = [];
      const { loadStandardFilterSuggestionsSuccess } = createQueryNodeModalActions('standard');
      const action = loadStandardFilterSuggestionsSuccess(newOptions, 0, 0, 0, 0);
      const updatedState = reducer(state, action);

      expect(updatedState[0].elements[0].tables[0].filters[0].options).to.deep.equal(options);
    });
  });
});