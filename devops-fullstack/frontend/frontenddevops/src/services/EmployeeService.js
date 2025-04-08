import axios from 'axios';

// *** URL complète du backend ***
// Pointe vers le serveur Spring Boot qui écoute sur le port 8080
const API_BASE_URL = 'http://localhost:8080/api';

// Fonction pour récupérer tous les employés
const getEmployees = () => {
    // Utilise l'URL de base + le chemin spécifique '/employees'
    return axios.get(`${API_BASE_URL}/employees`);
};

// Fonction pour calculer les heures supplémentaires
const calculateOvertime = (employeId, startDate, endDate) => {
    // Les dates doivent être au format YYYY-MM-DD
    const params = {
        startDate: startDate, // ex: '2024-04-01'
        endDate: endDate     // ex: '2024-04-10'
    };
    // Appelle GET /api/employees/{employeId}/overtime?startDate=...&endDate=...
    return axios.get(`${API_BASE_URL}/employees/${employeId}/overtime`, { params });
};

// Fonction pour ajouter des heures supplémentaires
const addHeureSup = (heureSupData) => {
    // heureSupData est un objet JS, par exemple:
    // { date: "2024-04-10", nbHeures: 2.5, employe: { id: 1 } }
    // Appelle POST /api/heures-sup
    return axios.post(`${API_BASE_URL}/heures-sup`, heureSupData);
};

// Regroupe et exporte les fonctions du service
const EmployeeService = {
    getAllEmployees: getEmployees, // Expose la fonction
    calculateOvertime,
    addHeureSup
};

export default EmployeeService;