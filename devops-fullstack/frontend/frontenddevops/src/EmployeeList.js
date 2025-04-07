import React, { useState, useEffect } from 'react';
// Importer le service
import EmployeeService from './services/EmployeeService'; // Assure-toi que ce chemin est correct

function EmployeeList() {
    const [employees, setEmployees] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        setLoading(true);
        setError(null);

        // Appeler la fonction du service
        EmployeeService.getAllEmployees()
            .then(response => {
                // Récupérer les données depuis response.data avec Axios
                setEmployees(response.data);
                console.log('Employés chargés:', response.data);
            })
            .catch(error => {
                console.error("Erreur lors du chargement des employés:", error);
                // Essayer d'afficher une erreur plus utile
                let errorMsg = "Impossible de charger les employés.";
                 if (error.response) {
                    // Le serveur a répondu avec un statut autre que 2xx
                    errorMsg += ` (Erreur serveur: ${error.response.status})`;
                    console.error("Détails erreur serveur:", error.response.data);
                 } else if (error.request) {
                    // La requête a été faite mais aucune réponse reçue (ex: backend éteint, pb réseau)
                    errorMsg += " Le serveur backend ne répond pas.";
                 } else {
                    // Erreur lors de la configuration de la requête
                     errorMsg += ` Détails: ${error.message}`;
                 }
                setError(errorMsg);
            })
            .finally(() => {
                setLoading(false);
            });
    }, []); // Tableau vide pour exécution unique au montage

    if (loading) {
        return <div>Chargement des employés...</div>;
    }

    if (error) {
        return <div style={{ color: 'red' }}>Erreur : {error}</div>;
    }

    return (
        <div>
            <h2>Liste des Employés</h2>
            {employees.length > 0 ? (
                <ul>
                    {employees.map(employee => (
                        <li key={employee.id}>
                            {employee.nom} {employee.prenom} - {employee.poste}
                        </li>
                    ))}
                </ul>
            ) : (
                <p>Aucun employé à afficher.</p>
            )}
        </div>
    );
}

export default EmployeeList;