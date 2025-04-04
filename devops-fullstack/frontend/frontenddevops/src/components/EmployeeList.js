import React, { useState, useEffect } from 'react';

function EmployeeList() {
    const [employees, setEmployees] = useState([]);

    useEffect(() => {
        // useEffect est un hook qui s'exécute après le rendu du composant
        fetch('/api/employes') // Remarquez l'URL relative grâce au proxy configuré
        .then(response => {
            console.log('response.status',response.status)
            return response.json();
        })
            .then(data => {
              console.log('employees',data);
              setEmployees(data)
            }) // Met à jour l'état avec les données récupérées
            .catch(error =>  console.log(error));// Le tableau vide en second argument indique que cet effet ne s'exécute qu'une seule fois (au montage du composant)
    }, []); // Le tableau vide en second argument indique que cet effet ne s'exécute qu'une seule fois (au montage du composant)

    return (
        <div>
            <h2>Liste des Employés</h2>
            <ul>
                {employees.map(employee => ( // Parcours la liste des employés
                    <li key={employee.id}> {/* La clé est importante pour React */}
                        {employee.nom} {employee.prenom} - {employee.poste}
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default EmployeeList;