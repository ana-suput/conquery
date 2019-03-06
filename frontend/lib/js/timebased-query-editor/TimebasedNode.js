// @flow

import React from "react";
import T from "i18n-react";
import styled from "@emotion/styled";
import classnames from "classnames";
import { DragSource } from "react-dnd";

import VerticalToggleButton, {
  Option
} from "../form-components/VerticalToggleButton";
import {
  EARLIEST,
  LATEST,
  RANDOM
} from "../common/constants/timebasedQueryTimestampTypes";
import { TIMEBASED_NODE } from "../common/constants/dndTypes";

import IconButton from "../button/IconButton";

const StyledIconButton = styled(IconButton)`
  position: absolute;
  top: 0;
  right: 0;
  z-index: 1;
  display: none;
`;

const Root = styled("div")`
  display: table-cell;
  vertical-align: middle;
  margin: 0 auto;
  width: 200px;
  font-size: ${({ theme }) => theme.font.sm};
`;

const StyledVerticalToggleButton = styled(VerticalToggleButton)`
  ${Option} {
    border: 0;

    &:first-of-type,
    &:last-of-type {
      border-radius: 0;
    }
  }
`;

type PropsType = {
  node: Object,
  position: "left" | "right",
  isIndexResult: boolean,
  onRemove: Function,
  onSetTimebasedNodeTimestamp: Function,
  onSetTimebasedIndexResult: Function,
  conditionIdx: number,
  resultIdx: number,
  connectDragSource: Function,
  isIndexResultDisabled: boolean
};

const TimebasedNode = (props: PropsType) => {
  const toggleButton = (
    <StyledVerticalToggleButton
      onToggle={props.onSetTimebasedNodeTimestamp}
      activeValue={props.node.timestamp}
      options={[
        {
          label: T.translate("timebasedQueryEditor.timestampFirst"),
          value: EARLIEST
        },
        {
          label: T.translate("timebasedQueryEditor.timestampRandom"),
          value: RANDOM
        },
        {
          label: T.translate("timebasedQueryEditor.timestampLast"),
          value: LATEST
        }
      ]}
    />
  );

  return (
    <Root
      ref={instance => {
        props.connectDragSource(instance);
      }}
    >
      <div className="timebased-node__container">
        <div className="timebased-node__content">
          <div className="timebased-node__timestamp">
            <p className="timebased-node__timestamp__title">
              {T.translate("timebasedQueryEditor.timestamp")}
            </p>
            {toggleButton}
          </div>
          <div className="timebased-node__description">
            <StyledIconButton icon="close" onClick={props.onRemove} />
            <p className="timebased-node__description__text">
              {props.node.label || props.node.id}
            </p>
          </div>
        </div>
        <button
          className={classnames("timebased-node__index-result-btn", {
            "timebased-node__index-result-btn--active": props.isIndexResult,
            "timebased-node__index-result-btn--disabled":
              props.isIndexResultDisabled
          })}
          disabled={props.isIndexResultDisabled}
          onClick={props.onSetTimebasedIndexResult}
        >
          {T.translate("timebasedQueryEditor.timestampResultsFrom")}
        </button>
      </div>
    </Root>
  );
};

/**
 * Implements the drag source contract.
 */
const nodeSource = {
  beginDrag(props) {
    // Return the data describing the dragged item
    const { node, conditionIdx, resultIdx } = props;

    return {
      conditionIdx,
      resultIdx,
      node,
      moved: true
    };
  }
};

/**
 * Specifies the dnd-related props to inject into the component.
 */
function collect(connect, monitor) {
  return {
    connectDragSource: connect.dragSource(),
    isDragging: monitor.isDragging()
  };
}

const DraggableTimebasedNode = DragSource(TIMEBASED_NODE, nodeSource, collect)(
  TimebasedNode
);

export default DraggableTimebasedNode;
