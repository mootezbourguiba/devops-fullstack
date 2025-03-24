import React from 'react';
import EmployeeList from './EmployeeList';
import OvertimeCalculator from './OvertimeCalculator';

function App() {
    return (
        <div>
            <h1>Application de Gestion des Employés</h1>
            <EmployeeList />
            <OvertimeCalculator />
        </div>
    );
}

export default App;