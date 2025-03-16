import React, { useState, useEffect } from 'react';
import mockEmployees from '../data/mock-employees'; // Import mock data

function EmployeeList() {
  const [employees, setEmployees] = useState([]);
  const [selectedEmployeeId, setSelectedEmployeeId] = useState(null);
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [overtimeHours, setOvertimeHours] = useState(0);

  useEffect(() => {
    // Simulate fetching data from an API
    setTimeout(() => {
      setEmployees(mockEmployees);
    }, 500); // Simulate a 500ms delay
  }, []);

  const handleEmployeeClick = (employeeId) => {
    setSelectedEmployeeId(employeeId);
    setOvertimeHours(0); // Reset overtime hours when a new employee is selected
    setStartDate(''); // Reset start date
    setEndDate(''); // Reset end date
  };

  const calculateOvertime = () => {
    // Basic Validation (you'll need more robust validation)
    if (!startDate || !endDate || !selectedEmployeeId) {
      alert("Please select an employee and enter start and end dates.");
      return;
    }

    // In real app, you would fetch overtime data from the backend API
    // and perform the calculation
    const calculatedHours = Math.floor(Math.random() * 10) + 1; // Random number for now
    setOvertimeHours(calculatedHours);
  };

  return (
    <div>
      <h2>Employee List</h2>
      <ul>
        {employees.map(employee => (
          <li
            key={employee.id}
            onClick={() => handleEmployeeClick(employee.id)}
            style={{ cursor: 'pointer', fontWeight: selectedEmployeeId === employee.id ? 'bold' : 'normal' }}
          >
            {employee.nom} {employee.prenom} - {employee.poste}
          </li>
        ))}
      </ul>

      {selectedEmployeeId && (
        <div>
          <h3>Calculate Overtime for Employee ID: {selectedEmployeeId}</h3>
          <label>Start Date:</label>
          <input
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
          />
          <label>End Date:</label>
          <input
            type="date"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
          />
          <button onClick={calculateOvertime}>Calculate Overtime</button>
          {overtimeHours > 0 && (
            <p>Overtime Hours: {overtimeHours}</p>
          )}
        </div>
      )}
    </div>
  );
}

export default EmployeeList;