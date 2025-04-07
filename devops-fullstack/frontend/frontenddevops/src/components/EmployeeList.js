import React, { useState, useEffect } from 'react';
// *** ETAPE 1: Importer le service ***
import EmployeeService from '../services/EmployeeService'; // Ajuste le chemin si nécessaire

function EmployeeList() {
    const [employees, setEmployees] = useState([]);
    // Optionnel : Ajouter un état pour le chargement et les erreurs
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        setLoading(true); // Commence le chargement
        setError(null);   // Réinitialise les erreurs précédentes

        // *** ETAPE 2: Appeler la fonction du service ***
        EmployeeService.getAllEmployees() // Utilise la fonction importée
            .then(response => {
                // Axios enveloppe la réponse dans 'data'
                setEmployees(response.data); // Met à jour l'état avec les données (response.data avec Axios)
                console.log('Employés chargés:', response.data);
            })
            .catch(error => {
                console.error("Erreur lors du chargement des employés:", error);
                setError("Impossible de charger les employés. Vérifiez la console."); // Met à jour l'état d'erreur
            })
            .finally(() => {
                setLoading(false); // Fin du chargement (succès ou échec)
            });
    }, []); // Le tableau vide assure que l'effet s'exécute une seule fois au montage

    // Affichage pendant le chargement
    if (loading) {
        return <div>Chargement des employés...</div>;
    }

    // Affichage en cas d'erreur
    if (error) {
        return <div style={{ color: 'red' }}>Erreur : {error}</div>;
    }

    // Affichage de la liste si tout va bien
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
                <p>Aucun employé à afficher.</p> // Message si la liste est vide
            )}
        </div>
    );
}

export default EmployeeList;