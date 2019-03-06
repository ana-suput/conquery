import React from "react";
import styled from "@emotion/styled";

import BasicButton from "./BasicButton";

const TransparentButton = styled(BasicButton)`
  color: ${({ theme, light }) => (light ? theme.col.gray : theme.col.black)};
  background-color: transparent;
  border-radius: 2px;
  border: 1px solid
    ${({ theme, light }) => (light ? theme.col.grayLight : theme.col.gray)};

  &:hover {
    background-color: ${({ theme }) => theme.col.grayVeryLight};
  }

  &:focus {
    border: 1px solid ${({ theme }) => theme.col.green};
    background-color: ${({ theme }) => theme.col.grayVeryLight};
  }
`;

export default props => <TransparentButton {...props} />;
