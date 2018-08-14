import React, { Component } from 'react';
import './styles.css';
import { Button, Input, Form, Row, Col, Label, FormGroup } from 'reactstrap';
import { withCookies } from 'react-cookie';
import { withRouter } from 'react-router-dom';
import { QUESTIONS } from '../Routes';

class App extends Component {
  constructor(){
    super();
    this.state = {}
    this.login = this.login.bind(this);
  }

  login() {
    fetch('http://localhost:8080/user/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(this.state)
    })
    .then((res) => res.text())
    .then((jwt) => {
      this.props.cookies.set('token', jwt);
      this.props.history.push(QUESTIONS); 
    });
  }

  render() {
    return (
      <div className="Login-container">
        <Row>
          <Col xs="12" md={{ size: 8, offset: 2 }}>
            <Form className="form">
              <FormGroup>
                <Label for="name">Name</Label>
                <Input
                  type="name"
                  name="name"
                  id="name"
                  placeholder="name"
                  value={this.state.name}
                  onChange={e => this.setState({name: e.target.value})}
                />
              </FormGroup>
              <FormGroup>
                <Label for="password">Password</Label>
                <Input
                  type="password"
                  name="password"
                  id="password"
                  placeholder="password"
                  value={this.state.password}
                  onChange={e => this.setState({password: e.target.value})}
                />
              </FormGroup>
              <Button onClick={this.login}>Log in</Button>
            </Form>
          </Col>
        </Row>
      </div>
    );
  }
}

export default withRouter(withCookies(App));
