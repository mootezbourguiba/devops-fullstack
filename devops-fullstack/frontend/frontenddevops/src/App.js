import React from 'react';
import EmployeeList from './components/EmployeeList';
import './App.css';

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <h1>Employee Management</h1>
      </header>
      <main>
        <EmployeeList />
      </main>
    </div>
  );
}

export default App;