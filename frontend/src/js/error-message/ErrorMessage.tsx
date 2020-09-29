import React from "react";
import styled from "@emotion/styled";

type PropsType = {
  className?: string;
  message: string;
};

const Root = styled("p")`
  color: ${({ theme }) => theme.col.red};
  font-weight: 400;
`;

const ErrorMessage = ({ className, message }: PropsType) => {
  return <Root className={className}>{message}</Root>;
};

export default ErrorMessage;
