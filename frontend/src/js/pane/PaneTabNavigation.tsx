import { StateT } from "app-types";
import React from "react";
import { useSelector, useDispatch } from "react-redux";

import TabNavigation, { TabNavigationTab } from "./TabNavigation";
import { clickPaneTab } from "./actions";

interface PropsT {
  paneType: "left" | "right";
  tabs: TabNavigationTab[];
}

const PaneTabNavigation: React.FC<PropsT> = ({ tabs, paneType }) => {
  const activeTab = useSelector<StateT, string | null>(
    (state) => state.panes[paneType].activeTab,
  );
  const dispatch = useDispatch();

  const onClickTab = (tab: string) => dispatch(clickPaneTab(paneType, tab));

  return (
    <TabNavigation onClickTab={onClickTab} activeTab={activeTab} tabs={tabs} />
  );
};

export default PaneTabNavigation;
