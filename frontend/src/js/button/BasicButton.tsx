import React from "react";
import styled from "@emotion/styled";

interface PropsT extends React.HTMLAttributes<HTMLButtonElement> {
  bare?: boolean;
  tiny?: boolean;
  small?: boolean;
  large?: boolean;
  active?: boolean;
}

const Button = styled("button")<PropsT>`
  cursor: pointer;
  font-weight: ${({ active }) => (active ? "700" : "400")};
  padding: ${({ small, tiny, bare, large }) =>
    bare
      ? "0"
      : tiny
      ? "4px 6px"
      : small
      ? "6px 10px"
      : large
      ? "12px 18px"
      : "8px 15px"};
  font-size: ${({ theme, small, tiny, large }) =>
    tiny || small ? theme.font.xs : large ? theme.font.lg : theme.font.sm};
  transition: all 0.2s;
  border-radius: ${({ theme }) => theme.borderRadius};

  &:disabled {
    cursor: not-allowed;
    opacity: 0.6;
  }
`;

const BasicButton: React.FC<PropsT> = ({ children, ...props }) => (
  <Button type="button" {...props}>
    {children}
  </Button>
);

export default BasicButton;
