import styled from "@emotion/styled";
import React, { FC } from "react";

import FaIcon from "../icon/FaIcon";
import WithTooltip from "../tooltip/WithTooltip";

const Root = styled("div")`
  margin-bottom: 8px;
  border-bottom: 1px solid ${({ theme }) => theme.col.grayLight};
  padding: 0 20px;
  background-color: white;
  display: flex;
  align-items: flex-start;
`;

const Headline = styled("h2")<{ active: boolean }>`
  font-size: ${({ theme }) => theme.font.sm};
  margin-bottom: 0;
  margin-top: 6px;
  padding: 0 12px;
  letter-spacing: 1px;
  line-height: 30px;
  text-transform: uppercase;
  flex-shrink: 0;

  transition: color ${({ theme }) => theme.transitionTime},
    border-bottom ${({ theme }) => theme.transitionTime};
  cursor: pointer;
  margin-right: 5px;
  color: ${({ theme, active }) =>
    active ? theme.col.blueGrayDark : theme.col.gray};
  border-bottom: 3px solid
    ${({ theme, active }) => (active ? theme.col.blueGrayDark : "transparent")};

  &:hover {
    color: ${({ theme, active }) =>
      active ? theme.col.blueGrayDark : theme.col.black};
    border-bottom: 3px solid
      ${({ theme, active }) =>
        active ? theme.col.blueGrayDark : theme.col.grayLight};
  }
`;

const SxWithTooltip = styled(WithTooltip)`
  flex-shrink: 0;
`;

const SxFaIcon = styled(FaIcon)`
  margin-left: 5px;
`;

export interface TabNavigationTab {
  key: string;
  label: string;
  tooltip?: string;
  loading?: boolean;
}

interface PropsT {
  onClickTab: (tab: string) => void;
  activeTab: string | null;
  tabs: TabNavigationTab[];
}

const TabNavigation: FC<PropsT> = ({ tabs, activeTab, onClickTab }) => {
  return (
    <Root>
      {tabs.map(({ key, label, tooltip, loading }) => {
        return (
          <SxWithTooltip text={tooltip} lazy key={key}>
            <Headline
              active={activeTab === key}
              onClick={() => {
                if (key !== activeTab) onClickTab(key);
              }}
            >
              {label}
              {loading && <SxFaIcon icon="spinner" />}
            </Headline>
          </SxWithTooltip>
        );
      })}
    </Root>
  );
};

export default TabNavigation;
