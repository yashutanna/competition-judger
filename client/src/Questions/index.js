import React, { Component } from 'react';
import './styles.css';
import { Card, CardBody, CardTitle, CardSubtitle, Row, Col, Button } from 'reactstrap';
import { withCookies } from 'react-cookie';
import { withRouter } from 'react-router-dom';
import { startCase } from 'lodash';

const fetchAuthenticated = (url, token, method, body) =>
  fetch(url, {
    method: method || 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body,
  })
  .then((res) => res.json())

class App extends Component {
  constructor(){
    super();
    this.state = {}
  }

  componentWillMount() {
    const token = this.props.cookies.get('token');
    fetchAuthenticated('http://localhost:8080/questions/', token)
    .then((questions) => {
      console.log(questions)
      this.setState({
        questions,
      })
    });
  }

  getSmallTestSet = (questionName) => () => {
    const token = this.props.cookies.get('token');
    fetchAuthenticated(`http://localhost:8080/questions/${questionName}/small-set`, token)
    .then((questions) => {
      console.log(questions)
      this.setState({
        questions,
      })
    });
  }

  getLargeTestSet = (questionName) => () => {
    const token = this.props.cookies.get('token');
    fetchAuthenticated(`http://localhost:8080/questions/${questionName}/large-set`, token)
    .then((questions) => {
      console.log(questions)
      this.setState({
        questions,
      })
    });
  }

  render() {
    const { questions } = this.state;
    return (
      <div>
        <h3 className="text-center">Questions</h3>
        <Row>
          {
            questions && questions.map(question => (
              <Col xs="12" md="6">
                <Card className="Question-card">
                  <CardBody>
                    <CardTitle className="text-center">{startCase(question.name)}</CardTitle>
                    <CardSubtitle className="Question_Subtitle text-center">Time limit <span className="text-primary">{question.timeLimit}</span> seconds</CardSubtitle>
                    <Button className="float-left" onClick={this.getSmallTestSet(question.name)}>Small Test Set</Button>
                    <Button className="float-right" onClick={this.getLargeTestSet(question.name)}>Large Test Set</Button>
                  </CardBody>
                </Card>
              </Col>
            ))
          }
        </Row>
      </div>
    );
  }
}

export default withRouter(withCookies(App));
