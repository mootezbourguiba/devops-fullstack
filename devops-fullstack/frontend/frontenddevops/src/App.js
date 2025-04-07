import React from 'react';
// Importe les composants depuis leur emplacement (directement dans src/)
import EmployeeList from './EmployeeList';
import OvertimeCalculator from './OvertimeCalculator';
// Importe le nouveau composant depuis le sous-dossier components/
import AddHeureSupForm from './components/AddHeureSupForm'; // Chemin correct car AddHeureSupForm est dans components
import './App.css'; // Importe le CSS

function App() {
    return (
        <div className="container"> {/* Conteneur principal */}
            <h1>Application de Gestion des Employés</h1>

            {/* Affiche la liste des employés */}
            <EmployeeList />

            {/* Ajoute un séparateur visuel (optionnel mais recommandé) */}
            <hr style={{ margin: '20px 0' }} />

            {/* Affiche le calculateur d'heures sup */}
            <OvertimeCalculator />

             {/* Ajoute un autre séparateur visuel */}
             <hr style={{ margin: '20px 0' }} />

            {/* Affiche le formulaire d'ajout d'heures sup */}
            <AddHeureSupForm /> {/* <-- LIGNE AJOUTEE */}

        </div>
    );
}

export default App;