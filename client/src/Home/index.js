import React, { Component } from 'react';
import logo from './logo.png';
import './styles.css';

class App extends Component {
  constructor(){
    super();
    this.state = {
      name: "",
      password: "",
    }
  }
  render() {
    return (
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <h1 className="App-title">Standard Bank Tech Impact Challenge 2018</h1>
        </header>
        <form>
          <input name="name" type="text" value={this.state.name}/>
          <input name="password" type="password" value={this.state.password}/>
        </form>
      </div>
    );
  }
}

export default App;
