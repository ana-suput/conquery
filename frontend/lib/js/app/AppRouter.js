// @flow

import React from "react";
import { Route, Switch, Router } from "react-router";
import type { TabT } from "../pane/types";

import { LoginPage, WithAuthToken } from "../authorization";

import App from "./App";

type PropsType = {
  history: Object,
  rightTabs: TabT[]
};

const AppRouter = ({ history, ...rest }: PropsType) => {
  return (
    <Router history={history}>
      <Switch>
        <Route path="/login" component={LoginPage} />
        <Route
          path="/*"
          render={routeProps => (
            <WithAuthToken {...routeProps}>
              <App {...rest} />
            </WithAuthToken>
          )}
        />
      </Switch>
    </Router>
  );
};

export default AppRouter;
