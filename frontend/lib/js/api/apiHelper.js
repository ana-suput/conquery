// @flow

// Functions in this file help to transform data from our state into a shape
// that is compatible with the backend api
//
// Mainly, certain keys are whitelisted
// (to exclude others that are relevant to the frontend only)
// Some keys are added (e.g. the query type attribute)

// TODO: Use, once feature is complete
// import {
//   DAYS_BEFORE,
//   DAYS_OR_NO_EVENT_BEFORE,
// } from '../common/constants/timebasedQueryOperatorTypes';

import { isEmpty } from "../common/helpers";

import { type TableType } from "../standard-query-editor/types";

export const transformTablesToApi = (tables: TableType[]) => {
  if (!tables) return [];

  return tables
    .filter(table => !table.exclude)
    .map(table => {
      // Explicitly whitelist the tables that we allow to send to the API
      return {
        id: table.connectorId,
        filters: table.filters
          ? table.filters
              .filter(filter => !isEmpty(filter.value)) // Only send filters with a value
              .map(filter => ({
                filter: filter.id,
                type: filter.type,
                value:
                  filter.value instanceof Array
                    ? filter.value.map(v => (v.value ? v.value : v))
                    : filter.value
              }))
          : []
      };
    });
};

export const transformElementGroupsToApi = elementGroups =>
  elementGroups.map(elements => ({
    matchingType: elements.matchingType,
    type: "OR",
    children: transformElementsToApi(elements.concepts)
  }));

export const transformElementsToApi = conceptGroup =>
  conceptGroup.map(createConcept);

const transformStandardQueryToApi = query =>
  createConceptQuery(createQueryConcepts(query));

const createConceptQuery = children => ({
  type: "CONCEPT_QUERY",
  root: {
    type: "AND",
    children: children
  }
});

const createNegation = group => ({
  type: "NEGATION",
  child: group
});

const createDateRestriction = (dateRange, concept) => ({
  type: "DATE_RESTRICTION",
  dateRange: dateRange,
  child: concept
});

const createSavedQuery = conceptId => ({
  type: "SAVED_QUERY",
  query: conceptId
});

const createQueryConcept = concept =>
  concept.isPreviousQuery
    ? createSavedQuery(concept.id)
    : createConcept(concept);

const createConcept = concept => ({
  type: "CONCEPT",
  ids: concept.ids,
  label: concept.label,
  excludeFromTimeAggregation: concept.excludeTimestamps,
  tables: transformTablesToApi(concept.tables)
});

const createQueryConcepts = query => {
  return query.map(group => {
    const concepts = group.dateRange
      ? group.elements.map(concept =>
          createDateRestriction(group.dateRange, createQueryConcept(concept))
        )
      : group.elements.map(concept => createQueryConcept(concept));

    var result =
      group.elements.length > 1
        ? { type: "OR", children: [...concepts] }
        : concepts.reduce((acc, curr) => ({ ...acc, ...curr }), {});

    return group.exclude ? createNegation(result) : result;
  });
};

// TODO: Use, once feature is complete
// const getDayRange = (condition) => {
//   if (condition.operator === DAYS_BEFORE)
//     return [
//       { minDays: condition.minDays },
//       { maxDays: condition.maxDays },
//     ];

//   if (condition.operator === DAYS_OR_NO_EVENT_BEFORE)
//     return [
//       { minDays: condition.minDaysOrNoEvent },
//       { maxDays: condition.maxDaysOrNoEvent },
//     ];

//   return [{}, {}];
// };

const transformTimebasedQueryToApi = query => ({
  type: "CONCEPT_QUERY",
  root: {
    type: "AND",
    children: query.conditions.map(condition => {
      // TODO: Use, once feature is complete
      // const [ minDays, maxDays ] = getDayRange(condition);
      return {
        type: condition.operator,
        sampler: "EARLIEST",
        index: createSavedQuery(condition.result0.id),
        preceding: createSavedQuery(condition.result1.id)
      };
    })
  }
});

const transformExternalQueryToApi = query =>
  createConceptQuery(createExternal(query));

const createExternal = (query: any) => ({
  type: "EXTERNAL",
  format: query.data[0],
  values: [query.data.slice(1)]
});

// The query state already contains the query.
// But small additions are made (properties whitelisted), empty things filtered out
// to make it compatible with the backend API
export const transformQueryToApi = (query: Object, queryType: string) => {
  switch (queryType) {
    case "timebased":
      return transformTimebasedQueryToApi(query);
    case "standard":
      return transformStandardQueryToApi(query);
    case "external":
      return transformExternalQueryToApi(query);
    default:
      return null;
  }
};
