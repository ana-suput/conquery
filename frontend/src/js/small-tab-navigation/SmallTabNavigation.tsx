import React, { FC } from "react";

import SmallTabNavigationButton from "./SmallTabNavigationButton";

interface TabOption {
  label: string;
  value: string;
}

interface PropsT {
  className?: string;
  size?: "M" | "L";
  options: TabOption[];
  selectedTab: string;
  onSelectTab: (tab: string) => void;
}

const SmallTabNavigation: FC<PropsT> = ({
  className,
  size = "M",
  options,
  selectedTab,
  onSelectTab,
}) => {
  return (
    <div className={className}>
      {options.map((option) => (
        <SmallTabNavigationButton
          key={option.value}
          value={option.value}
          size={size}
          isSelected={selectedTab === option.value}
          onClick={() => onSelectTab(option.value)}
        >
          {option.label}
        </SmallTabNavigationButton>
      ))}
    </div>
  );
};

export default SmallTabNavigation;
