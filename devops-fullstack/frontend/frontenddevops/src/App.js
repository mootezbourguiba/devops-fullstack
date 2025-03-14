import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

function App() {
  // Créer un état pour stocker les données récupérées depuis le backend
  const [items, setItems] = useState([]);

  // Utiliser useEffect pour effectuer la requête API lors du chargement du composant
  useEffect(() => {
    // Faire une requête GET vers ton backend Spring Boot
    axios.get('http://localhost:8080/api/items')  // Remplace cette URL par l'URL de ton API
      .then(response => {
        // Si la requête est réussie, on met à jour l'état avec les données
        setItems(response.data);
      })
      .catch(error => {
        // Gérer les erreurs d'appel API
        console.error('Erreur lors de la récupération des données:', error);
      });
  }, []); // [] signifie que cela ne s'exécutera qu'une seule fois, lors du premier rendu

  return (
    <div className="App">
      <header className="App-header">
        <h1>Items from Backend</h1>
        <ul>
          {/* Afficher les éléments récupérés depuis l'API */}
          {items.length > 0 ? (
            items.map(item => (
              <li key={item.id}>{item.name}</li>  // Assure-toi que les propriétés 'id' et 'name' existent dans la réponse
            ))
          ) : (
            <p>Loading...</p>  // Afficher un message de chargement si les données ne sont pas encore disponibles
          )}
        </ul>
      </header>
    </div>
  );
}

export default App;
