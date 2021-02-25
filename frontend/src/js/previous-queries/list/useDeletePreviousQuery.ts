import { useSelector, useDispatch } from "react-redux";
import { StateT } from "app-types";

import type { DatasetIdT } from "../../api/types";
import { useDeleteStoredQuery } from "../../api/api";
import { setMessage } from "../../snack-message/actions";
import type { PreviousQueryIdT } from "./reducer";
import { deletePreviousQuerySuccess } from "./actions";

export const useDeletePreviousQuery = (
  previousQueryId: PreviousQueryIdT,
  onSuccess?: () => void
) => {
  const datasetId = useSelector<StateT, DatasetIdT | null>(
    (state) => state.datasets.selectedDatasetId
  );
  const dispatch = useDispatch();
  const deleteStoredQuery = useDeleteStoredQuery();

  return async () => {
    if (!datasetId) return;

    try {
      await deleteStoredQuery(datasetId, previousQueryId);

      dispatch(deletePreviousQuerySuccess(previousQueryId));

      if (onSuccess) {
        onSuccess();
      }
    } catch (e) {
      dispatch(setMessage("previousQuery.deleteError"));
    }
  };
};
