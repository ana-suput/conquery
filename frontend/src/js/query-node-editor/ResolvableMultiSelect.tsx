import React, { FC, useEffect, useState } from "react";

import { usePostFilterValuesResolve } from "../api/api";
import type {
  FilterIdT,
  PostFilterResolveResponseT,
  SelectOptionT,
} from "../api/types";
import { getUniqueFileRows } from "../common/helpers/fileHelper";
import { usePrevious } from "../common/helpers/usePrevious";
import AsyncInputMultiSelect from "../form-components/AsyncInputMultiSelect";
import InputMultiSelect, {
  MultiSelectInputProps,
} from "../form-components/InputMultiSelect";

import type { FiltersContextT } from "./TableFilters";
import UploadFilterListModal from "./UploadFilterListModal";

interface FilterContextT extends FiltersContextT {
  filterId: FilterIdT;
}

interface PropsT {
  context: FilterContextT;

  label: string;
  options: SelectOptionT[];
  disabled?: boolean;
  tooltip?: string;
  allowDropFile?: boolean;

  isLoading?: boolean;
  onLoad?: (prefix: string) => void;
  startLoadingThreshold: number;

  input: MultiSelectInputProps;
}

const ResolvableMultiSelect: FC<PropsT> = ({
  context,
  input,
  label,
  options,
  disabled,
  allowDropFile,

  startLoadingThreshold,
  onLoad,
  isLoading,
}) => {
  const [resolved, setResolved] = useState<PostFilterResolveResponseT | null>(
    null,
  );
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<boolean>(false);
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const postFilterValuesResolve = usePostFilterValuesResolve();

  const previousDefaultValue = usePrevious(input.defaultValue);

  // Can be both, an auto-completable (async) multi select or a regular one
  const Component = !!onLoad ? AsyncInputMultiSelect : InputMultiSelect;

  const onDropFile = async (file: File) => {
    setLoading(true);

    const rows = await getUniqueFileRows(file);

    try {
      const r = await postFilterValuesResolve(
        context.datasetId,
        context.treeId,
        context.tableId,
        context.filterId,
        rows,
      );

      setResolved(r);
      setIsModalOpen(!!r.unknownCodes && r.unknownCodes.length > 0);

      if (
        r.resolvedFilter &&
        r.resolvedFilter.value &&
        r.resolvedFilter.value.length > 0
      ) {
        input.onChange(r.resolvedFilter.value);
      }
    } catch (e) {
      setError(true);
    }

    setLoading(false);
  };

  useEffect(() => {
    async function resolveDefaultValue() {
      const hasDefaultValueToLoad =
        input.defaultValue &&
        input.defaultValue.length > 0 &&
        JSON.stringify(input.defaultValue) !==
          JSON.stringify(previousDefaultValue);

      if (hasDefaultValueToLoad) {
        const r = await postFilterValuesResolve(
          context.datasetId,
          context.treeId,
          context.tableId,
          context.filterId,
          input.defaultValue as string[],
        );

        if (
          r.resolvedFilter &&
          r.resolvedFilter.value &&
          r.resolvedFilter.value.length > 0
        ) {
          input.onChange(r.resolvedFilter.value);
        }
      }
    }
    resolveDefaultValue();
  }, [
    context.datasetId,
    context.filterId,
    context.tableId,
    context.treeId,
    previousDefaultValue,
    input,
    postFilterValuesResolve,
  ]);

  return (
    <>
      {allowDropFile && isModalOpen && (
        <UploadFilterListModal
          resolved={resolved}
          loading={loading}
          error={error}
          onClose={() => setIsModalOpen(false)}
        />
      )}
      <Component
        input={input}
        label={label}
        options={options}
        isLoading={isLoading || loading}
        startLoadingThreshold={startLoadingThreshold}
        disabled={disabled}
        onLoad={onLoad}
        onDropFile={onDropFile}
        allowDropFile={allowDropFile}
      />
    </>
  );
};

export default ResolvableMultiSelect;
