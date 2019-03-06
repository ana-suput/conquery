// @flow

import React from "react";
import { type FieldPropsType } from "redux-form";

import { type SelectOptionsType } from "../common/types/backend";
import InputMultiSelect from "./InputMultiSelect";

type PropsType = FieldPropsType & {
  label: string,
  isLoading: boolean,
  options: SelectOptionsType,
  disabled?: ?boolean,
  startLoadingThreshold: number,
  tooltip?: string,
  onLoad: Function,
  onDropFile: Function,
  allowDropFile?: ?boolean
};

const AsyncInputMultiSelect = ({
  label,
  options,
  disabled,
  tooltip,
  startLoadingThreshold,
  onLoad,
  isLoading,
  input,
  onDropFile,
  allowDropFile
}: PropsType) => (
  <InputMultiSelect
    label={label}
    options={options}
    disabled={disabled}
    tooltip={tooltip}
    isLoading={isLoading}
    input={input}
    onInputChange={value => {
      if (value.length >= startLoadingThreshold) onLoad(value);

      return value;
    }}
    onDropFile={onDropFile}
    allowDropFile={allowDropFile}
  />
);

export default AsyncInputMultiSelect;
