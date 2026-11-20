import { Link, NavLink, Route, Routes } from 'react-router-dom';
import { CategoriasPage } from './pages/CategoriasPage';
import { EventosPage } from './pages/EventosPage';
import { ParticipantesPage } from './pages/ParticipantesPage';

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
                        <NavLink to="/participantes">Participantes</NavLink>
                        <NavLink to="/categorias">Categorias</NavLink>
                    </nav>
                </div>
            </header>
            <main className="container">
                <Routes>
                    <Route path="/" element={<EventosPage />} />
                    <Route path="/participantes" element={<ParticipantesPage />} />
                    <Route path="/categorias" element={<CategoriasPage />} />
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
