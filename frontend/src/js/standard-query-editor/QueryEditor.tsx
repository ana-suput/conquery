import React from "react";
import styled from "@emotion/styled";

import QueryGroupModal from "../query-group-modal/QueryGroupModal";
import QueryUploadConceptListModal from "../query-upload-concept-list-modal/QueryUploadConceptListModal";

import Query from "./Query";
import StandardQueryNodeEditor from "./StandardQueryNodeEditor";

const Root = styled("div")`
  flex-grow: 1;
  height: 100%;
  overflow: auto;
  padding: 0 10px 10px 10px;
`;

export const QueryEditor = () => {
  return (
    <Root>
      <Query />
      <StandardQueryNodeEditor />
      <QueryUploadConceptListModal />
      <QueryGroupModal />
    </Root>
  );
};
