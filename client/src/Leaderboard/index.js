import React, { Component } from 'react';
import './styles.css';
import { Table } from 'reactstrap';
import { withCookies } from 'react-cookie';
import { withRouter } from 'react-router-dom';

const questionSorter = (a, b) => {
  if (a.name < b.name) {
    return -1;
  }
  if (a.name > b.name) {
    return 1;
  }
  return 0;
}

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
    }
  }

  componentWillMount() {
    const token = this.props.cookies.get('token');
    fetchAuthenticated('http://localhost:8080/leaderboard/', token)
    .then((res) => res.json())
    .then((leaderboard) => {
      this.setState({
        leaderboard,
      })
    });
    fetchAuthenticated('http://localhost:8080/questions/', token)
    .then((res) => res.json())
    .then((questions) => {
      this.setState({
        questions,
      })
    });
  }

  render() {
    const { leaderboard, questions } = this.state;
    return (
      <div>
        <h3 className="text-center">Leaderboard</h3>
        {
          questions && leaderboard &&
            <Table>
              <thead>
                <tr>
                  <th>University</th>
                  {
                    questions.sort(questionSorter).map(question => (
                      <th className="text-center">{question.name}</th>
                    ))
                  }
                </tr>
              </thead>
              <tbody>
                {
                  leaderboard.map((standing) => (
                    <tr>
                      <td>{standing.university}</td>
                      {
                        questions.sort(questionSorter).map(question => (
                          <td className="text-center">{standing.submissions[question.name] === true ? 'PASS' : standing.submissions[question.name] === false ? 'FAIL' : ''}</td>
                        ))
                      }
                    </tr>
                  ))
                }
              </tbody>
            </Table>
        }
      </div>
    );
  }
}

export default withRouter(withCookies(App));
