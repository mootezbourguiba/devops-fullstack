import { render, screen } from '@testing-library/react';
import App from './App';

// On modifie le nom du test pour qu'il soit plus précis
test('renders main application heading', () => {
  // On affiche le composant App dans un environnement de test virtuel
  render(<App />);

  // On recherche un élément qui contient le texte "Application de Gestion des Employés"
  // L'expression régulière /.../i permet d'ignorer la casse (majuscules/minuscules)
  const headingElement = screen.getByText(/Application de Gestion des Employés/i);

  // On s'attend à ce que cet élément soit présent dans le document (DOM virtuel)
  expect(headingElement).toBeInTheDocument();
});

// Optionnel : Tu pourrais ajouter d'autres tests ici si tu le souhaites, par exemple:
// test('shows loading message initially', () => {
//   render(<App />);
//   const loadingElement = screen.getByText(/Chargement des employés.../i);
//   expect(loadingElement).toBeInTheDocument();
// });