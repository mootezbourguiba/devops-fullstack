import React, { useState } from 'react';
import EmployeeService from '../services/EmployeeService'; // Vérifie le chemin d'import

function AddHeureSupForm() {
    const [employeId, setEmployeId] = useState('');
    const [date, setDate] = useState('');
    const [nbHeures, setNbHeures] = useState('');
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage('');
        setError('');
        setLoading(true);

        if (!employeId || !date || !nbHeures || nbHeures <= 0) {
            setError("Veuillez remplir tous les champs correctement (heures > 0).");
            setLoading(false);
            return;
        }

        const heureSupData = {
            date: date,
            nbHeures: parseFloat(nbHeures), // Convertit en nombre
            employe: {
                id: parseInt(employeId, 10) // Convertit en nombre entier base 10
            }
        };

        try {
            const response = await EmployeeService.addHeureSup(heureSupData);
            setMessage(`Heures supplémentaires ajoutées avec succès pour l'employé ID ${employeId} (ID HeureSup: ${response.data.id})`);
            // Optionnel: Réinitialiser le formulaire
            setEmployeId('');
            setDate('');
            setNbHeures('');
        } catch (err) {
            console.error("Erreur lors de l'ajout des heures sup:", err);
            let errorMsg = "Erreur lors de l'ajout.";
            if (err.response && err.response.data && err.response.data.error) {
                errorMsg = `Erreur: ${err.response.data.error}`;
            } else if (err.message) {
                errorMsg = err.message;
            }
            setError(errorMsg);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{ marginTop: '20px', borderTop: '1px solid #ccc', paddingTop: '20px' }}>
            <h2>Ajouter des Heures Supplémentaires</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>ID Employé:</label>
                    <input
                        type="number"
                        value={employeId}
                        onChange={e => setEmployeId(e.target.value)}
                        placeholder="ex: 1"
                        required
                    />
                </div>
                <div>
                    <label>Date:</label>
                    <input
                        type="date"
                        value={date}
                        onChange={e => setDate(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Nombre d'Heures:</label>
                    <input
                        type="number"
                        step="0.1" // Permet les décimales
                        min="0.1" // Minimum 0.1 heure
                        value={nbHeures}
                        onChange={e => setNbHeures(e.target.value)}
                        placeholder="ex: 2.5"
                        required
                    />
                </div>
                <button type="submit" disabled={loading}>
                    {loading ? 'Ajout en cours...' : 'Ajouter'}
                </button>

                {message && <div style={{ color: 'green', marginTop: '10px' }}>{message}</div>}
                {error && <div style={{ color: 'red', marginTop: '10px' }}>{error}</div>}
            </form>
        </div>
    );
}

export default AddHeureSupForm;