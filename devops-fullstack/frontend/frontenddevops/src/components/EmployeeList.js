import React, { useState, useEffect } from 'react';
import axios from 'axios'; // Import Axios

function EmployeeList() {
  const [employees, setEmployees] = useState([]);
  const [selectedEmployee, setSelectedEmployee] = useState(null);
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [overtimeHours, setOvertimeHours] = useState(null); //Can be null to confirm setup
  const [error, setError] = useState(null); // State for error handling

  useEffect(() => {
    // Simulate fetching data from an API
    fetchEmployees();
  }, []);

    //Function for backend - to call code and is sync for all async calls
    const fetchEmployees = async () => {
      try {
        const response = await axios.get('/api/employees'); // Adjust endpoint if needed
        setEmployees(response.data); // Set real data here
        setError(null); // Clear any previous errors
      } catch (err) {
        console.error("Error fetching employees:", err);
        setError("Failed to load employees. Please try again later."); // Set an error message
      }
    };


  const handleEmployeeClick = (employee) => {
    setSelectedEmployee(employee);
    setOvertimeHours(null); // Reset overtime hours to null when a new employee is selected
    setStartDate(''); // Reset start date
    setEndDate(''); // Reset end date
    setError(null) ;
  };

  const calculateOvertime = async () => {
        // Basic Validation (you'll need more robust validation)
        if (!startDate || !endDate || !selectedEmployee) {
          setError("Please select an employee and enter start and end dates.");
          setOvertimeHours(null);
          return;
        }

          setError(null) ;
      try {
          // Call the backend API to calculate overtime
          const response = await axios.get(`/api/overtime?employeeId=${selectedEmployee.id}&startDate=${startDate}&endDate=${endDate}`); // Adjust endpoint

          // Update state with the returned data
          setOvertimeHours(response.data.overtimeHours);

      } catch (err) {
          console.error("Error calculating overtime:", err);
          setOvertimeHours(null);
          setError("Failed to calculate overtime. Please try again later."); // Set an error message
      }
  };

  return (
    <div style={{ fontFamily: 'Arial, sans-serif', padding: '20px', backgroundColor: '#f4f4f4' }}>
      <h2 style={{ marginBottom: '20px', textAlign: 'center', color: '#333' }}>Employee List</h2>
      {error && <p style={{ color: 'red', textAlign: 'center' }}>{error}</p>} {/* Display error message if there is one */}
      <ul style={{ listStyleType: 'none', padding: 0, display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: '10px' }}>
        {employees.map(employee => (
          <li
            key={employee.id}
            onClick={() => handleEmployeeClick(employee)}
            style={{
              cursor: 'pointer',
              padding: '10px',
              borderBottom: '1px solid #eee',
              backgroundColor: selectedEmployee && selectedEmployee.id === employee.id ? '#f0f0f0' : 'transparent',
              borderRadius: '5px',
              boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)',
              transition: 'background-color 0.3s ease'
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center' }}>
              <span style={{ marginRight: '10px', fontSize: '18px', fontWeight: 'bold' }}>{employee.nom} {employee.prenom}</span>
              <span style={{ color: '#666' }}>{employee.poste}</span>
            </div>
          </li>
        ))}
      </ul>

      {selectedEmployee && (
        <div style={{ marginTop: '20px', padding: '15px', border: '1px solid #ccc', borderRadius: '5px', boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)', backgroundColor: '#fff' }}>
          <h3 style={{ marginBottom: '15px', textAlign: 'center', color: '#333' }}>Calculate Overtime for {selectedEmployee.nom} {selectedEmployee.prenom}</h3>
          {error && <p style={{ color: 'red', textAlign: 'center' }}>{error}</p>}
          <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
            <div style={{ marginBottom: '10px', display: 'flex', alignItems: 'center' }}>
              <label style={{ marginRight: '10px', fontSize: '16px', color: '#666' }}>Start Date:</label>
              <input
                type="date"
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
                style={{ padding: '8px', borderRadius: '3px', border: '1px solid #ddd', fontSize: '16px' }}
              />
            </div>
            <div style={{ marginBottom: '10px', display: 'flex', alignItems: 'center' }}>
              <label style={{ marginRight: '10px', fontSize: '16px', color: '#666' }}>End Date:</label>
              <input
                type="date"
                value={endDate}
                onChange={(e) => setEndDate(e.target.value)}
                style={{ padding: '8px', borderRadius: '3px', border: '1px solid #ddd', fontSize: '16px' }}
              />
            </div>
            <button onClick={calculateOvertime} style={{ padding: '8px 15px', backgroundColor: '#4CAF50', color: 'white', border: 'none', borderRadius: '3px', cursor: 'pointer', fontSize: '16px', transition: 'background-color 0.3s ease' }}>
              Calculate Overtime
            </button>
            {overtimeHours !== null  && ( //Check not null to load for all UI setup - I added this to all and should fix in most code if that happens
              <p style={{ marginTop: '10px', fontSize: '18px', color: '#333' }}>Overtime Hours: {overtimeHours}</p>
            )}
          </div>
        </div>
      )}
    </div>
  );
}

export default EmployeeList;