import React, { Component } from 'react';
import './styles.css';
import { Card, CardBody, CardTitle, CardSubtitle, Row, Col, Button, Input, Form, FormGroup, Label } from 'reactstrap';
import { withCookies } from 'react-cookie';
import { withRouter } from 'react-router-dom';
import { startCase } from 'lodash';
import moment from 'moment';

const fileDownload = require('js-file-download');

const fetchAuthenticated = (url, token, method, body) =>
  fetch(url, {
    method: method || 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body,
  })

class App extends Component {
  constructor(){
    super();
    this.state = {
      requestedSubmission: false,
    }
    this.selectFileToUpload = this.selectFileToUpload.bind(this);
    this.uploadFile = this.uploadFile.bind(this);
  }

  componentWillMount() {
    const token = this.props.cookies.get('token');
    fetchAuthenticated('http://localhost:8080/questions/', token)
    .then((res) => res.json())
    .then((questions) => {
      console.log(questions)
      this.setState({
        questions,
      })
    });
  }

  selectFileToUpload = (e) => {
    var data = new FormData()
    data.append('file', e.target.files[0])
    this.setState({
      fileToUpload: data,
    })
  }
  
  uploadFile = () => {
    const { fileToUpload } = this.state;
    const token = this.props.cookies.get('token');
    fetch('http://localhost:8080/submissions/', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
      },
      body: fileToUpload
    })
    .then((res) => res.json())
    .then((res) => {
      // debugger;
      console.log(res);
    })
  }

  getSmallTestSet = (questionName) => () => {
    const token = this.props.cookies.get('token');
    fetchAuthenticated(`http://localhost:8080/questions/${questionName}/small-set`, token)
    .then((res) => res.text())
    .then((questions) => {
      fileDownload(questions, `${questionName}_small_${moment().valueOf()}`)
      this.setState({
        requestedSubmission: true
      })
    });
  }

  getLargeTestSet = (questionName) => () => {
    const token = this.props.cookies.get('token');
    fetchAuthenticated(`http://localhost:8080/questions/${questionName}/large-set`, token)
    .then((res) => res.text())
    .then((questions) => {
      fileDownload(questions, `${questionName}_large_${moment().valueOf()}`)
      this.setState({
        requestedSubmission: true
      })
    });
  }

  render() {
    const { questions, requestedSubmission } = this.state;
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
                    <div>
                      <Button className="float-left" onClick={this.getSmallTestSet(question.name)}>Small Test Set</Button>
                      <Button className="float-right" onClick={this.getLargeTestSet(question.name)}>Large Test Set</Button>
                    </div>                    
                  </CardBody>
                </Card>
              </Col>
            ))
          }
        </Row>
        {
          requestedSubmission && (  
            <Row>
              <Col xs="12">
                <Card className="Question-card">
                  <CardBody>
                    <CardTitle className="text-center">Submit Answer</CardTitle>
                    <Form className="Question-submission-form">
                      <FormGroup>
                        <Input
                          type="file"
                          name="file"
                          id="file"
                          onChange={this.selectFileToUpload}
                        />
                      </FormGroup>
                      <Button color="primary" onClick={this.uploadFile}>Submit</Button>
                    </Form>                 
                  </CardBody>
                </Card>
              </Col>
            </Row>
          )
        }

      </div>
    );
  }
}

export default withRouter(withCookies(App));
