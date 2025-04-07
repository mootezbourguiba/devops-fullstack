import axios from 'axios';

// *** CORRECTION: URL complète du backend ***
// Pointe vers le serveur Spring Boot qui écoute sur le port 8080
const API_BASE_URL = 'http://localhost:8080/api';

// Renomme la fonction pour correspondre à ce qu'elle fait et utilise la nouvelle URL
const getEmployees = () => {
    // *** CORRECTION: Utilise l'URL de base + le chemin spécifique ***
    // Note: L'endpoint dans EmployeController est /employees (avec un 's')
    return axios.get(`${API_BASE_URL}/employees`);
};

// *** NOUVELLE FONCTION : Calculer les heures sup ***
const calculateOvertime = (employeId, startDate, endDate) => {
    // Les dates doivent être au format YYYY-MM-DD
    const params = {
        startDate: startDate, // ex: '2024-04-01'
        endDate: endDate     // ex: '2024-04-10'
    };
    // Appelle GET /api/employees/{employeId}/overtime?startDate=...&endDate=...
    return axios.get(`${API_BASE_URL}/employees/${employeId}/overtime`, { params });
};

// *** NOUVELLE FONCTION : Ajouter des heures sup ***
const addHeureSup = (heureSupData) => {
    // heureSupData est un objet JS, par exemple:
    // {
    //   date: "2024-04-10", // Format YYYY-MM-DD
    //   nbHeures: 2.5,
    //   employe: {
    //     id: 1 // ID de l'employé auquel associer ces heures
    //   }
    // }
    // Appelle POST /api/heures-sup avec les données dans le corps de la requête
    return axios.post(`${API_BASE_URL}/heures-sup`, heureSupData);
};

// Regroupe et exporte les fonctions du service
const EmployeeService = {
    // Expose la fonction renommée
    getAllEmployees: getEmployees, // Garde le nom externe si les composants l'utilisent déjà
    calculateOvertime,
    addHeureSup
    // Ajoute d'autres fonctions ici si nécessaire (getHeuresSup, etc.)
};

export default EmployeeService;