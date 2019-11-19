// @flow

import * as React from "react";
import styled from "@emotion/styled";
import { css } from "@emotion/core";

import Label from "./Label";

const Root = styled("label")`
  ${({ fullWidth }) =>
    fullWidth &&
    css`
      width: 100%;
      input {
        width: 100%;
      }
    `};
  }
`;

type PropsType = {
  label: React.Node,
  className?: string,
  tinyLabel?: boolean,
  fullWidth?: boolean,
  valueChanged?: boolean,
  disabled?: boolean,
  children?: React.Node
};

const Labeled = ({
  className,
  valueChanged,
  fullWidth,
  disabled,
  label,
  tinyLabel,
  children
}: PropsType) => {
  return (
    <Root
      className={className}
      valueChanged={valueChanged}
      fullWidth={fullWidth}
      disabled={disabled}
    >
      <Label fullWidth={fullWidth} tiny={tinyLabel}>
        {label}
      </Label>
      {children}
    </Root>
  );
};

export default Labeled;
