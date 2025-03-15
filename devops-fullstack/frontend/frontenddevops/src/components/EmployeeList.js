import React, { useState, useEffect } from 'react';
import EmployeeService from '../services/EmployeeService';

function EmployeeList() {
  const [employees, setEmployees] = useState([]);

  useEffect(() => {
    EmployeeService.getAllEmployees()
      .then(response => {
        setEmployees(response.data);
      })
      .catch(error => {
        console.error("Error fetching employees:", error);
      });
  }, []);

  return (
    <div>
      <h2>Employee List</h2>
      <ul>
        {employees.map(employee => (
          <li key={employee.id}>
            {employee.nom} {employee.prenom} - {employee.poste}
          </li>
        ))}
      </ul>
    </div>
  );
}

export default EmployeeList;