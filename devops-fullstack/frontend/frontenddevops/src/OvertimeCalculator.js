import React, { useState } from 'react';
// *** ETAPE 1: Importer le service ***
import EmployeeService from './services/EmployeeService'; // Ajuste le chemin si nécessaire

function OvertimeCalculator() {
    const [employeeId, setEmployeeId] = useState('');
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');
    // Modifier pour stocker la valeur ou l'erreur
    const [overtimeResult, setOvertimeResult] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError(null);
        setOvertimeResult(null); // Réinitialise le résultat précédent

        // Validation simple (peut être améliorée)
        if (!employeeId || !startDate || !endDate) {
            setError("Veuillez remplir tous les champs.");
            setLoading(false);
            return;
        }

        try {
            // *** ETAPE 2: Appeler la fonction du service ***
            // Passe l'ID et les dates au service
            const response = await EmployeeService.calculateOvertime(employeeId, startDate, endDate);
            // La réponse du backend est { "totalPay": valeur }
            setOvertimeResult(response.data.totalPay); // Stocke la valeur 'totalPay'
            console.log('Résultat calcul:', response.data);
        } catch (error) {
            console.error("Erreur lors du calcul des heures sup:", error);
            // Essayer d'afficher un message d'erreur plus précis si possible
            let errorMessage = "Erreur de calcul.";
            if (error.response && error.response.data && error.response.data.error) {
                errorMessage = `Erreur: ${error.response.data.error}`; // Affiche l'erreur du backend
            } else if (error.message) {
                errorMessage = error.message;
            }
            setError(errorMessage);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div>
            <h2>Calcul des Heures Supplémentaires</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>ID Employé:</label>
                    <input
                        type="number" // Garde type number pour la sémantique
                        value={employeeId}
                        onChange={e => setEmployeeId(e.target.value)} // La valeur sera une string, mais OK pour l'appel service
                        placeholder="ex: 1"
                        required // Ajoute required pour la validation navigateur
                    />
                </div>
                <div>
                    <label>Date de Début:</label>
                    <input
                        type="date"
                        value={startDate}
                        onChange={e => setStartDate(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Date de Fin:</label>
                    <input
                        type="date"
                        value={endDate}
                        onChange={e => setEndDate(e.target.value)}
                        required
                    />
                </div>
                <button type="submit" disabled={loading}> {/* Désactive le bouton pendant le chargement */}
                    {loading ? 'Calcul en cours...' : 'Calculer'}
                </button>

                {/* Affichage du résultat ou de l'erreur */}
                {error && <div style={{ color: 'red', marginTop: '10px' }}>{error}</div>}
                {overtimeResult !== null && !error && ( // Affiche seulement si pas d'erreur et résultat existe
                    <div style={{ marginTop: '10px' }}>
                        <h3>Total Heures Supplémentaires Payées : {overtimeResult.toFixed(2)} €</h3> {/* Exemple de formatage */}
                    </div>
                )}
            </form>
        </div>
    );
}

export default OvertimeCalculator;