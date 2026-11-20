import { Link, NavLink, Route, Routes } from 'react-router-dom';
import { EventosPage } from './pages/EventosPage';

export function App() {
  return (
    <>
      <header className="topbar">
        <div className="container topbar-inner">
          <Link to="/" className="brand">
            Sulwork <span className="brand-sub">Eventos colaborativos</span>
          </Link>
          <nav className="nav">
            <NavLink to="/" end>
              Eventos
            </NavLink>
          </nav>
        </div>
      </header>
      <main className="container">
        <Routes>
          <Route path="/" element={<EventosPage />} />
          <Route
            path="*"
            element={
              <div className="card">
                <p>Página não encontrada.</p>
                <Link to="/">Voltar para eventos</Link>
              </div>
            }
          />
        </Routes>
      </main>
    </>
  );
}
