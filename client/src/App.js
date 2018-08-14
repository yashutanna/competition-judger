import React, { Component } from 'react';
import { Switch, Route, Router } from 'react-router'

import Home from './Home/'

import createBrowserHistory from 'history/createBrowserHistory'

const history = createBrowserHistory()

const App = () => (
  <Router history={history}>
    <Switch>
      <Route exact path="/" component={Home}/>
      <Route path="/question" component={() => (
        <div>
            Question
        </div>
      )}/>
      <Route path="/submissions" component={() => (
        <div>
            Submission
        </div>
      )}/>
      <Route component={() => (
        <div>
            Not Found
        </div>
      )}/>
    </Switch>
  </Router>
);

export default App;
