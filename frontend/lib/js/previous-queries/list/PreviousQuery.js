// @flow

import React from "react";
import styled from "@emotion/styled";
import { css } from "@emotion/core";

import T from "i18n-react";
import { DragSource } from "react-dnd";
import { connect } from "react-redux";
import moment from "moment";

import { ErrorMessage } from "../../error-message";
import { dndTypes } from "../../common/constants";
import { SelectableLabel } from "../../selectable-label";

import DownloadButton from "../../button/DownloadButton";
import IconButton from "../../button/IconButton";

import { EditableText, EditableTags } from "../../form-components";

import { deletePreviousQueryModalOpen } from "../delete-modal/actions";

import { type DraggedQueryType } from "../../standard-query-editor/types";

import {
  toggleSharePreviousQuery,
  renamePreviousQuery,
  retagPreviousQuery,
  toggleEditPreviousQueryLabel,
  toggleEditPreviousQueryTags
} from "./actions";

import PreviousQueryTags from "./PreviousQueryTags";

const nodeSource = {
  beginDrag(props): DraggedQueryType {
    // Return the data describing the dragged item
    return {
      id: props.query.id,
      label: props.query.label,
      isPreviousQuery: true
    };
  }
};

// These props get injected into the component
function collect(connect, monitor) {
  return {
    connectDragSource: connect.dragSource(),
    isDragging: monitor.isDragging()
  };
}

const StyledIconButton = styled(IconButton)`
  margin-left: 10px;
`;
const HoverButton = styled(StyledIconButton)`
  display: none;
`;

const Root = styled("div")`
  margin: 0;
  padding: 5px 10px;
  cursor: pointer;
  border-radius: ${({ theme }) => theme.borderRadius};
  border: 1px solid ${({ theme }) => theme.col.grayLight};
  background-color: ${({ theme }) => theme.col.bg};

  border-left: ${({ theme, own, system }) =>
    own
      ? `4px solid ${theme.col.orange}`
      : system
      ? `4px solid ${theme.col.blueGrayDark}`
      : `1px solid ${theme.col.grayLight}`};

  &:hover {
    ${({ theme, own, system }) =>
      !own &&
      !system &&
      css`
        border-left-color: ${theme.col.blueGray};
      `};
    border-top-color: ${({ theme }) => theme.col.blueGray};
    border-right-color: ${({ theme }) => theme.col.blueGray};
    border-bottom-color: ${({ theme }) => theme.col.blueGray};

    ${HoverButton} {
      display: inline-block;
    }
  }
`;

const TopInfos = styled("div")`
  color: ${({ theme }) => theme.col.gray};
  margin: 0;
`;
const TopRight = styled("div")`
  float: right;
`;
const SharedIndicator = styled("span")`
  margin-left: 10px;
  color: ${({ theme }) => theme.col.blueGray};
`;
const StyledSelectableLabel = styled(SelectableLabel)`
  margin: 0;
  font-weight: 400;
  word-break: break-word;
`;
const StyledEditableText = styled(EditableText)`
  margin: 0;
  font-weight: 400;
  word-break: break-word;
`;
const MiddleRow = styled("div")`
  display: flex;
  width: 100%;
  justify-content: space-between;
`;
const StyledErrorMessage = styled(ErrorMessage)`
  margin: 0;
`;
const StyledEditableTags = styled(EditableTags)`
  margin-top: 5px;
`;

type PropsType = {
  query: {
    id: number | string,
    label: string,
    loading: boolean,
    numberOfResults: number,
    createdAt: string,
    tags: string[],
    own: boolean,
    shared: boolean
  },
  onRenamePreviousQuery: () => void,
  onToggleEditPreviousQueryLabel: () => void,
  onToggleEditPreviousQueryTags: () => void,
  onToggleSharePreviousQuery: () => void,
  onRetagPreviousQuery: () => void,
  onDeletePreviousQuery: () => void,
  connectDragSource: () => void,
  availableTags: string[]
};

const PreviousQuery = (props: PropsType) => {
  const {
    query,
    onRenamePreviousQuery,
    onToggleEditPreviousQueryTags,
    onToggleEditPreviousQueryLabel,
    onRetagPreviousQuery,
    onToggleSharePreviousQuery
  } = props;

  const peopleFound = `${query.numberOfResults} ${T.translate(
    "previousQueries.results"
  )}`;
  const executedAt = moment(query.createdAt).fromNow();
  const label = query.label || query.id.toString();
  const mayEditQuery = query.own || query.shared;
  const isNotEditing = !(query.editingLabel || query.editingTags);

  return (
    <Root
      ref={instance => {
        if (isNotEditing) props.connectDragSource(instance);
      }}
      own={!!query.own}
      shared={!!query.shared}
      system={!!query.system || (!query.own && !query.shared)}
    >
      <TopInfos>
        <div>
          {query.resultUrl ? (
            <DownloadButton bare large url={query.resultUrl}>
              {peopleFound}
            </DownloadButton>
          ) : (
            peopleFound
          )}
          {query.own &&
            (query.shared ? (
              <SharedIndicator
                onClick={() => onToggleSharePreviousQuery(!query.shared)}
              >
                {T.translate("previousQuery.shared")}
              </SharedIndicator>
            ) : (
              <StyledIconButton
                icon="upload"
                onClick={() => onToggleSharePreviousQuery(!query.shared)}
              >
                {T.translate("previousQuery.share")}
              </StyledIconButton>
            ))}
          {mayEditQuery && !query.editingTags && (
            <HoverButton
              icon="plus"
              large
              bare
              onClick={onToggleEditPreviousQueryTags}
            >
              {T.translate("previousQuery.addTag")}
            </HoverButton>
          )}
          <TopRight>
            {executedAt}
            {query.loading ? (
              <IconButton large bare icon="spinner" />
            ) : (
              query.own && (
                <IconButton
                  icon="close"
                  tiny
                  onClick={props.onDeletePreviousQuery}
                />
              )
            )}
          </TopRight>
        </div>
      </TopInfos>
      <MiddleRow>
        {mayEditQuery ? (
          <StyledEditableText
            loading={!!query.loading}
            text={label}
            selectTextOnMount={true}
            editing={!!query.editingLabel}
            onSubmit={onRenamePreviousQuery}
            onToggleEdit={onToggleEditPreviousQueryLabel}
          />
        ) : (
          <StyledSelectableLabel label={label} />
        )}
        <TopInfos>{query.ownerName}</TopInfos>
      </MiddleRow>
      {mayEditQuery ? (
        <StyledEditableTags
          tags={query.tags}
          editing={!!query.editingTags}
          loading={!!query.loading}
          onSubmit={onRetagPreviousQuery}
          onToggleEdit={onToggleEditPreviousQueryTags}
          tagComponent={<PreviousQueryTags tags={query.tags} />}
          availableTags={props.availableTags}
        />
      ) : (
        <PreviousQueryTags tags={query.tags} />
      )}
      {!!query.error && <StyledErrorMessage message={query.error} />}
    </Root>
  );
};

const mapStateToProps = state => ({
  availableTags: state.previousQueries.tags
});

const mapDispatchToProps = dispatch => ({
  onToggleSharePreviousQuery: (datasetId, queryId, shared) =>
    dispatch(toggleSharePreviousQuery(datasetId, queryId, shared)),

  onRenamePreviousQuery: (datasetId, queryId, label) =>
    dispatch(renamePreviousQuery(datasetId, queryId, label)),

  onRetagPreviousQuery: (datasetId, queryId, tags) =>
    dispatch(retagPreviousQuery(datasetId, queryId, tags)),

  onDeletePreviousQuery: queryId =>
    dispatch(deletePreviousQueryModalOpen(queryId)),

  onToggleEditPreviousQueryLabel: queryId =>
    dispatch(toggleEditPreviousQueryLabel(queryId)),

  onToggleEditPreviousQueryTags: queryId =>
    dispatch(toggleEditPreviousQueryTags(queryId))
});

const mapProps = (stateProps, dispatchProps, ownProps) => ({
  ...stateProps,
  ...dispatchProps,
  ...ownProps,
  onToggleSharePreviousQuery: shared =>
    dispatchProps.onToggleSharePreviousQuery(
      ownProps.datasetId,
      ownProps.query.id,
      shared
    ),
  onRenamePreviousQuery: label =>
    dispatchProps.onRenamePreviousQuery(
      ownProps.datasetId,
      ownProps.query.id,
      label
    ),
  onRetagPreviousQuery: tags =>
    dispatchProps.onRetagPreviousQuery(
      ownProps.datasetId,
      ownProps.query.id,
      tags
    ),
  onDeletePreviousQuery: () =>
    dispatchProps.onDeletePreviousQuery(ownProps.query.id),
  onToggleEditPreviousQueryLabel: () =>
    dispatchProps.onToggleEditPreviousQueryLabel(ownProps.query.id),
  onToggleEditPreviousQueryTags: () =>
    dispatchProps.onToggleEditPreviousQueryTags(ownProps.query.id)
});

export default DragSource(dndTypes.PREVIOUS_QUERY, nodeSource, collect)(
  connect(
    mapStateToProps,
    mapDispatchToProps,
    mapProps
  )(PreviousQuery)
);
