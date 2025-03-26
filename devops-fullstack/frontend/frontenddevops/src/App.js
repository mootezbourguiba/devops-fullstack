import React from 'react';
import EmployeeList from './EmployeeList';
import OvertimeCalculator from './OvertimeCalculator';
import './App.css';

function App() {
    return (
        <div className="container">
            <h1>Application de Gestion des Employés</h1>
            <EmployeeList />
            <OvertimeCalculator />
        </div>
    );
}

export default App;
