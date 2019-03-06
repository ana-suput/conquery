// @flow

import React from "react";
import styled from "@emotion/styled";
import type { Dispatch } from "redux-thunk";
import { connect } from "react-redux";
import T from "i18n-react";

import { type QueryNodeType } from "../standard-query-editor/types";

import TransparentButton from "../button/TransparentButton";
import EscAble from "../common/components/EscAble";

import MenuColumn from "./MenuColumn";
import NodeDetailsView from "./NodeDetailsView";
import TableView from "./TableView";
import DescriptionColumn from "./DescriptionColumn";

import { createQueryNodeEditorActions } from "./actions";

const StyledEscAble = styled(EscAble)`
  padding: 0 10px;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
  position: absolute;
  display: flex;
  background: rgb(249, 249, 249);
  z-index: 1;
`;

const Wrapper = styled("div")`
  border: 1px solid ${({ theme }) => theme.col.blueGrayDark};
  display: flex;
  flex-direction: row;
  width: 100%;
  height: 100%;
  overflow: auto;
`;

const CloseButton = styled(TransparentButton)`
  border: 1px solid ${({ theme }) => theme.col.blueGrayDark};
  position: absolute;
  bottom: 10px;
  right: 20px;
`;

type QueryNodeEditorState = {
  detailsViewActive: boolean,
  selectedInputTableIdx: number,
  selectedInput: number,
  editingLabel: boolean,
  onSelectDetailsView: Function,
  onSelectInputTableView: Function,
  onShowDescription: Function,
  onToggleEditLabel: Function,
  onReset: Function
};

export type PropsType = {
  name: string,
  editorState: QueryNodeEditorState,
  node: QueryNodeType,
  showTables: boolean,
  isExcludeTimestampsPossible: boolean,
  onCloseModal: Function,
  onUpdateLabel: Function,
  onDropConcept: Function,
  onDropFilterValuesFile: Function,
  onRemoveConcept: Function,
  onToggleTable: Function,
  onSetFilterValue: Function,
  onResetAllFilters: Function,
  onToggleTimestamps: Function,
  onSwitchFilterMode: Function,
  onLoadFilterSuggestions: Function,
  onSelectSelects: Function,
  onSelectTableSelects: Function,
  datasetId: number,
  suggestions: ?Object,
  onToggleIncludeSubnodes: Function
};

const QueryNodeEditor = (props: PropsType) => {
  const { node, editorState } = props;

  if (!node) return null;

  const selectedTable =
    !node.isPreviousQuery && editorState.selectedInputTableIdx != null
      ? node.tables[editorState.selectedInputTableIdx]
      : null;

  return (
    <StyledEscAble onEscPressed={props.onCloseModal}>
      <Wrapper>
        <MenuColumn {...props} />
        {editorState.detailsViewActive && <NodeDetailsView {...props} />}
        {!editorState.detailsViewActive && selectedTable != null && (
          <TableView {...props} />
        )}
        {!editorState.detailsViewActive && <DescriptionColumn {...props} />}
        <CloseButton
          small
          onClick={() => {
            editorState.onReset();
            props.onCloseModal();
          }}
        >
          {T.translate("common.done")}
        </CloseButton>
      </Wrapper>
    </StyledEscAble>
  );
};

export const createConnectedQueryNodeEditor = (
  mapStateToProps: Function,
  mapDispatchToProps: Function,
  mergeProps: Function
) => {
  const mapDispatchToPropsInternal = (dispatch: Dispatch, ownProps) => {
    const externalDispatchProps = mapDispatchToProps
      ? mapDispatchToProps(dispatch, ownProps)
      : {};

    const {
      setDetailsViewActive,
      toggleEditLabel,
      setInputTableViewActive,
      setFocusedInput,
      reset
    } = createQueryNodeEditorActions(ownProps.type);

    return {
      ...externalDispatchProps,
      editorState: {
        ...(externalDispatchProps.editorState || {}),
        onSelectDetailsView: () => dispatch(setDetailsViewActive()),
        onToggleEditLabel: () => dispatch(toggleEditLabel()),
        onSelectInputTableView: tableIdx =>
          dispatch(setInputTableViewActive(tableIdx)),
        onShowDescription: filterIdx => dispatch(setFocusedInput(filterIdx)),
        onReset: () => dispatch(reset())
      }
    };
  };

  const mergePropsInternal = (stateProps, dispatchProps, ownProps) => {
    const externalMergedProps = mergeProps
      ? mergeProps(stateProps, dispatchProps, ownProps)
      : { ...ownProps, ...stateProps, ...dispatchProps };

    return {
      ...externalMergedProps,
      editorState: {
        ...(stateProps.editorState || {}),
        ...(dispatchProps.editorState || {})
      }
    };
  };

  return connect(
    mapStateToProps,
    mapDispatchToPropsInternal,
    mergePropsInternal
  )(QueryNodeEditor);
};
