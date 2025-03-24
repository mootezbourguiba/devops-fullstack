import React, { useState } from 'react';

function OvertimeCalculator() {
    const [employeeId, setEmployeeId] = useState('');
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');
    const [overtime, setOvertime] = useState(null);

    const handleSubmit = async (e) => {
        e.preventDefault(); // Empêche le rechargement de la page lors de la soumission du formulaire

        try {
            const response = await fetch(`/api/employes/${employeeId}/overtime?startDate=${startDate}&endDate=${endDate}`);
            const data = await response.json();
            setOvertime(data);
        } catch (error) {
            console.error("Error calculating overtime:", error);
            setOvertime('Erreur de calcul'); // Indiquer une erreur à l'utilisateur
        }
    };

    return (
        <div>
            <h2>Calcul des Heures Supplémentaires</h2>
            <form onSubmit={handleSubmit}> {/* Gère la soumission du formulaire */}
                <div>
                    <label>ID Employé:</label>
                    <input
                        type="number"
                        value={employeeId}
                        onChange={e => setEmployeeId(e.target.value)} // Met à jour l'état lors de la saisie
                    />
                </div>
                <div>
                    <label>Date de Début:</label>
                    <input
                        type="date"
                        value={startDate}
                        onChange={e => setStartDate(e.target.value)}
                    />
                </div>
                <div>
                    <label>Date de Fin:</label>
                    <input
                        type="date"
                        value={endDate}
                        onChange={e => setEndDate(e.target.value)}
                    />
                </div>
                <button type="submit">Calculer</button>

                {overtime !== null && ( // Affiche le résultat seulement si overtime n'est pas null
                    <div>
                        <h3>Heures Supplémentaires: {overtime}</h3>
                    </div>
                )}
            </form>
        </div>
    );
}

export default OvertimeCalculator;