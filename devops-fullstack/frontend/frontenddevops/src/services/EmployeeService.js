import axios from 'axios';

const API_BASE_URL = '/api/employes'; // Uses the proxy setting

const EmployeeService = {
  getAllEmployees: () => {
    return axios.get(API_BASE_URL);
  },

  // Implement other API calls as needed (getEmployeeById, createEmployee, etc.)
};

export default EmployeeService;