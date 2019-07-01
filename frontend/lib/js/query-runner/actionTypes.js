// @flow

// QUERY RUNNER
export const START_TIMEBASED_QUERY_START =
  "query-runners/START_TIMEBASED_QUERY_START";
export const START_TIMEBASED_QUERY_SUCCESS =
  "query-runners/START_TIMEBASED_QUERY_SUCCESS";
export const START_TIMEBASED_QUERY_ERROR =
  "query-runners/START_TIMEBASED_QUERY_ERROR";
export const STOP_TIMEBASED_QUERY_START =
  "query-runners/STOP_TIMEBASED_QUERY_START";
export const STOP_TIMEBASED_QUERY_SUCCESS =
  "query-runners/STOP_TIMEBASED_QUERY_SUCCESS";
export const STOP_TIMEBASED_QUERY_ERROR =
  "query-runners/STOP_TIMEBASED_QUERY_ERROR";
export const QUERY_TIMEBASED_RESULT_START =
  "query-runners/QUERY_TIMEBASED_RESULT_START";
export const QUERY_TIMEBASED_RESULT_STOP =
  "query-runners/QUERY_TIMEBASED_RESULT_STOP";
export const QUERY_TIMEBASED_RESULT_SUCCESS =
  "query-runners/QUERY_TIMEBASED_RESULT_SUCCESS";
export const QUERY_TIMEBASED_RESULT_ERROR =
  "query-runners/QUERY_TIMEBASED_RESULT_ERROR";

export const START_STANDARD_QUERY_START =
  "query-runners/START_STANDARD_QUERY_START";
export const START_STANDARD_QUERY_SUCCESS =
  "query-runners/START_STANDARD_QUERY_SUCCESS";
export const START_STANDARD_QUERY_ERROR =
  "query-runners/START_STANDARD_QUERY_ERROR";
export const STOP_STANDARD_QUERY_START =
  "query-runners/STOP_STANDARD_QUERY_START";
export const STOP_STANDARD_QUERY_SUCCESS =
  "query-runners/STOP_STANDARD_QUERY_SUCCESS";
export const STOP_STANDARD_QUERY_ERROR =
  "query-runners/STOP_STANDARD_QUERY_ERROR";
export const QUERY_STANDARD_RESULT_START =
  "query-runners/QUERY_STANDARD_RESULT_START";
export const QUERY_STANDARD_RESULT_STOP =
  "query-runners/QUERY_STANDARD_RESULT_STOP";
export const QUERY_STANDARD_RESULT_SUCCESS =
  "query-runners/QUERY_STANDARD_RESULT_SUCCESS";
export const QUERY_STANDARD_RESULT_ERROR =
  "query-runners/QUERY_STANDARD_RESULT_ERROR";

//
// This is called "EXTERNAL",
// but it's used for the result list upload
// The reason is that the backend calls this
// query type "EXTERNAL", under the standard POST queries endpoint
//
export const START_EXTERNAL_QUERY_START =
  "query-runners/START_EXTERNAL_QUERY_START";
export const START_EXTERNAL_QUERY_SUCCESS =
  "query-runners/START_EXTERNAL_QUERY_SUCCESS";
export const START_EXTERNAL_QUERY_ERROR =
  "query-runners/START_EXTERNAL_QUERY_ERROR";
export const STOP_EXTERNAL_QUERY_START =
  "query-runners/STOP_EXTERNAL_QUERY_START";
export const STOP_EXTERNAL_QUERY_SUCCESS =
  "query-runners/STOP_EXTERNAL_QUERY_SUCCESS";
export const STOP_EXTERNAL_QUERY_ERROR =
  "query-runners/STOP_EXTERNAL_QUERY_ERROR";
export const QUERY_EXTERNAL_RESULT_START =
  "query-runners/QUERY_EXTERNAL_RESULT_START";
export const QUERY_EXTERNAL_RESULT_STOP =
  "query-runners/QUERY_EXTERNAL_RESULT_STOP";
export const QUERY_EXTERNAL_RESULT_SUCCESS =
  "query-runners/QUERY_EXTERNAL_RESULT_SUCCESS";
export const QUERY_EXTERNAL_RESULT_ERROR =
  "query-runners/QUERY_EXTERNAL_RESULT_ERROR";

// Now these are actually used for the external forms.
// External here means that this is code which is external to conquery
// => Will be merged into conquery soon
export const START_EXTERNAL_FORMS_QUERY_START =
  "query-runners/START_EXTERNAL_FORMS_QUERY_START";
export const START_EXTERNAL_FORMS_QUERY_SUCCESS =
  "query-runners/START_EXTERNAL_FORMS_QUERY_SUCCESS";
export const START_EXTERNAL_FORMS_QUERY_ERROR =
  "query-runners/START_EXTERNAL_FORMS_QUERY_ERROR";
export const STOP_EXTERNAL_FORMS_QUERY_START =
  "query-runners/STOP_EXTERNAL_FORMS_QUERY_START";
export const STOP_EXTERNAL_FORMS_QUERY_SUCCESS =
  "query-runners/STOP_EXTERNAL_FORMS_QUERY_SUCCESS";
export const STOP_EXTERNAL_FORMS_QUERY_ERROR =
  "query-runners/STOP_EXTERNAL_FORMS_QUERY_ERROR";
export const QUERY_EXTERNAL_FORMS_RESULT_START =
  "query-runners/QUERY_EXTERNAL_FORMS_RESULT_START";
export const QUERY_EXTERNAL_FORMS_RESULT_STOP =
  "query-runners/QUERY_EXTERNAL_FORMS_RESULT_STOP";
export const QUERY_EXTERNAL_FORMS_RESULT_SUCCESS =
  "query-runners/QUERY_EXTERNAL_FORMS_RESULT_SUCCESS";
export const QUERY_EXTERNAL_FORMS_RESULT_ERROR =
  "query-runners/QUERY_EXTERNAL_FORMS_RESULT_ERROR";
